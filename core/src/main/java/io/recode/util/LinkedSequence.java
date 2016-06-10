package io.recode.util;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class LinkedSequence<T> extends AbstractCollection<T> implements Sequence<T> {

    public static final class Creator implements SequenceCreator {
        @Override
        public <E> Sequence<E> createSequence() {
            return new LinkedSequence<>();
        }
    }

    private Link<T> first;

    private Link<T> last;

    private int size;

    private int version = 0;

    @Override
    public boolean add(T element) {
        assert element != null : "Element can't be null";

        final Link<T> newLink = new Link<>(element);

        if (first == null) {
            first = last = newLink;
        } else {
            last.next(newLink);
            newLink.previous(last);
            last = newLink;
        }

        size++;
        version++;

        return true;
    }

    @Override
    public SingleElement<T> last() {
        return new LinkSelector(last);
    }

    @Override
    public SingleElement<T> last(Predicate<T> predicate) {
        assert predicate != null : "Predicate can't be null";

        Link<T> current = last;

        while (current != null) {
            if (predicate.test(current.element())) {
                return new LinkSelector(current);
            }

            current = current.previous();
        }

        return new LinkSelector(null);
    }

    @Override
    public SingleElement<T> first() {
        return new LinkSelector(first);
    }

    @Override
    public SingleElement<T> first(Predicate<T> predicate) {
        assert predicate != null : "Predicate can't be null";

        Link<T> current = first;

        while (current != null) {
            if (predicate.test(current.element())) {
                return new LinkSelector(current);
            }

            current = current.next();
        }

        return new LinkSelector(null);
    }

    @Override
    public SingleElement<T> at(int index) {
        assert index >= 0 : "Index must be positive";

        Link<T> current = first;

        while (index-- > 0 && current != null) {
            current = current.next();
        }

        return new LinkSelector(current);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MultipleElements<T> all() {
        final Link<T>[] links = new Link[size];

        Link<T> current = first;

        for (int i = 0; i < size; i++) {
            links[i] = current;

            current = current.next;
        }

        final int snapshotVersion = version;

        return new MultipleElements<T>() {

            @Override
            public boolean exists() {
                return true;
            }

            @Override
            public List<T> get() {
                final ArrayList<T> copy = new ArrayList<>();

                for (Link<T> link :links) {
                    copy.add(link.element());
                }

                return copy;
            }

            @Override
            public void remove() {
                if (version == snapshotVersion) {
                    clear();
                } else {
                    for (Link link : links) {
                        removeLink(link);
                    }
                }
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public MultipleElements<T> tail(int offset) {
        if (offset < 0) {
            offset = size + offset;
        }

        if (offset < 0 || offset > size) {
            return NON_EXISTING_ELEMENTS;
        }

        final Link[] tail = new Link[size - offset];

        Link link = first;

        while (offset-- > 0 && link != null) {
            link = link.next;
        }

        int index = 0;

        while (link != null) {
            tail[index++] = link;
            link = link.next;
        }

        return new LinkArraySelector(tail);
    }

    private final class LinkArraySelector implements MultipleElements<T> {

        private final int snapshotVersion = version;

        private final Link<T>[] links;

        private LinkArraySelector(Link<T>[] links) {
            this.links = links;
        }

        @Override
        public List<T> get() {
            checkVersion();

            final ArrayList<T> copy = new ArrayList<>(links.length);

            for (Link<T> link : links) {
                copy.add(link.element);
            }

            return copy;
        }

        @Override
        public void remove() {
            checkVersion();

            for (Link<T> link : links) {
                removeLink(link);
            }
        }

        @Override
        public boolean exists() {
            checkVersion();
            return true;
        }

        private void checkVersion() {
            if (version != snapshotVersion) {
                throw new ConcurrentModificationException();
            }
        }
    }

    @Override
    public void clear() {
        first = last = null;
        size = 0;
        version++;
    }

    @Override
    public boolean isEmpty() {
        return first == null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        return new SuppliedIterator<>(new Supplier<Optional<T>>() {

            private final int version = LinkedSequence.this.version;

            private Link<T> current = first;

            @Override
            public Optional<T> get() {
                if (current == null) {
                    return Optional.empty();
                }

                if (LinkedSequence.this.version != version) {
                    throw new ConcurrentModificationException();
                }

                final T element = current.element;
                current = current.next;
                return Optional.of(element);
            }
        });
    }

    private void removeLink(Link<T> link) {
        if (link.previous == null) {
            first = link.next;
        } else {
            link.previous.next = link.next;
        }

        if (link.next == null) {
            last = link.previous;
        } else {
            link.next.previous = link.previous;
        }

        size--;
        version++;
    }

    private static final class Link<T> {

        private T element;

        private Link<T> previous;

        private Link<T> next;

        private Link(T element) {
            this.element = element;
        }

        T element() {
            return element;
        }

        void element(T element) {
            assert element != null : "Element can't be null";
            this.element = element;
        }

        Link<T> previous() {
            return previous;
        }

        void previous(Link<T> previous) {
            this.previous = previous;
        }

        Link<T> next() {
            return next;
        }

        void next(Link<T> next) {
            this.next = next;
        }

    }

    private class LinkSelector implements SingleElement<T> {

        private Link<T> link;

        private int snapshotVersion;

        private LinkSelector(Link<T> link) {
            this.link = link;
            this.snapshotVersion = version;
        }

        @Override
        public void swap(T newElement) {
            assert newElement != null : "Element can't be null";

            checkExists();
            checkVersion();

            link.element(newElement);

            upgrade();
        }

        @Override
        public boolean exists() {
            return (link != null);
        }

        @Override
        public T get() {
            checkExists();
            checkVersion();

            return link.element();
        }

        @Override
        public void insertBefore(T element) {
            assert element != null : "Element can't be null";

            checkExists();
            checkVersion();

            final Link<T> newLink = new Link<>(element);

            newLink.previous = link.previous;
            newLink.next = link;

            if (link.previous == null) {
                first = newLink;
            } else {
                link.previous.next = newLink;
            }

            link.previous = newLink;

            size++;

            upgrade();
        }

        @Override
        public SingleElement<T> previous() {
            checkExists();
            checkVersion();
            return new LinkSelector(link.previous);
        }

        @Override
        public T remove() {
            checkExists();
            checkVersion();

            removeLink(link);

            snapshotVersion = version;

            return link.element;
        }

        private void upgrade() {
            snapshotVersion = ++version;
        }

        private void checkExists() {
            if (!exists()) {
                throw new NoSuchElementException();
            }
        }

        private void checkVersion() {
            if (version != snapshotVersion) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private static final MultipleElements NON_EXISTING_ELEMENTS = new MultipleElements() {
        @Override
        public List<Object> get() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new NoSuchElementException();
        }

        @Override
        public boolean exists() {
            return false;
        }
    };
}
