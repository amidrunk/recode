import React from 'react'

export default class MainPage extends React.Component {

    render() {
        return (
            <div className="panel-group">
                <h1 className="page-header">Recode</h1>
                Recode is a framework for decompiling code during runtime. This allows deep introspection into code
                spanning well beyond the power of reflection. Using Recode you can e.g. decompile the caller of a method,
                decompile an arbitrary method, an adjacent line of code or similar into an abstract syntax tree that
                can be used for analysis or code generation.

                <div className="panel-group">
                    <h5>Example - code regeneration</h5>
                    
                    <code>
                        final Recode recode = Recode.withDefaults();<br />
                        final List&lt;CodePointer&lt;?&gt;&gt; codePointers = recode.decompile(Caller.get());<br />
                        final String code = recode.generate(JavaSyntax.get());
                    </code>
                </div>

                <div className="panel-group">
                    <h5>Example - code transpiling</h5>

                    <code>
                        final Recode recode = Recode.withDefaults();<br />
                        final List&lt;CodePointer&lt;?&gt;&gt; codePointers = recode.decompile(Caller.get());<br />
                        final String code = recode.generate(JavaScriptSyntax.get());
                    </code>
                </div>

            </div>
        );
    }
}