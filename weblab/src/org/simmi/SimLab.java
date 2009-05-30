package org.simmi;

import java.io.Console;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class SimLab implements ScriptEngineFactory {
	Simple	engine = new Simple();
	
	public class Simple implements ScriptEngine {
		char[]	cbuf = new char[256];
		
		@Override
		public Bindings createBindings() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object eval(String script) throws ScriptException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object eval(Reader reader) throws ScriptException {
			try {
				int r = reader.read(cbuf);
				if( r > 0 ) {
					return new String( cbuf, 0, r );
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		public Object eval(String script, ScriptContext context) throws ScriptException {
			return null;
		}

		@Override
		public Object eval(Reader reader, ScriptContext context) throws ScriptException {
			return null;
		}

		@Override
		public Object eval(String script, Bindings n) throws ScriptException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object eval(Reader reader, Bindings n) throws ScriptException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object get(String key) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Bindings getBindings(int scope) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ScriptContext getContext() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ScriptEngineFactory getFactory() {
			return SimLab.this;
		}

		@Override
		public void put(String key, Object value) {
			
		}

		@Override
		public void setBindings(Bindings bindings, int scope) {
			
		}

		@Override
		public void setContext(ScriptContext context) {
			
		}
	};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {	
		Console console = System.console();
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		List<ScriptEngineFactory> scriptEngineFactories = scriptEngineManager.getEngineFactories();
		for( ScriptEngineFactory scriptEngineFactory : scriptEngineFactories ) {
			System.err.println( scriptEngineFactory.getEngineName() );
		}
		
		SimLab  simlab = new SimLab();
		ScriptEngine engine = simlab.getScriptEngine();
		try {
			Object obj = engine.eval( console.reader() );
			while( obj != null && !obj.equals("quit\n")) {
				System.err.println( obj );
				obj = engine.eval( console.reader() );
			}
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getEngineName() {
		return "Simlab";
	}

	@Override
	public String getEngineVersion() {
		return "1.0";
	}

	@Override
	public List<String> getExtensions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLanguageName() {
		return "Simple";
	}

	@Override
	public String getLanguageVersion() {
		return "1.0";
	}

	@Override
	public String getMethodCallSyntax(String obj, String m, String... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getMimeTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOutputStatement(String toDisplay) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getParameter(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProgram(String... statements) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScriptEngine getScriptEngine() {
		return engine;
	}
}