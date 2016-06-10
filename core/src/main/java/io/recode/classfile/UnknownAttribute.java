package io.recode.classfile;

import java.io.InputStream;

public interface UnknownAttribute extends Attribute {

    InputStream getData();

}
