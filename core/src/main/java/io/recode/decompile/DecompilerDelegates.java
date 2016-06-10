package io.recode.decompile;

import io.recode.decompile.CodeStream;
import io.recode.decompile.DecompilationContext;
import io.recode.decompile.DecompilerDelegate;
import io.recode.decompile.DecompilerElementDelegate;
import io.recode.model.ModelQuery;

import java.io.IOException;
import java.util.Optional;

public final class DecompilerDelegates {

    public static<R> ForQueryContinuation<R> forQuery(ModelQuery<DecompilationContext, R> query) {
        assert query != null : "Query can't be null";

        return new ForQueryContinuation<R>() {
            @Override
            public DecompilerDelegate apply(DecompilerElementDelegate<R> transformation) {
                assert transformation != null : "Transformation can't be null";

                return new DecompilerDelegate() {
                    @Override
                    public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                        final Optional<R> result = query.from(context);

                        if (result.isPresent()) {
                            transformation.apply(context, codeStream, byteCode, result.get());
                        }
                    }
                };
            }
        };
    }

    public interface ForQueryContinuation<R> {

        DecompilerDelegate apply(DecompilerElementDelegate<R> transformation);

    }

}
