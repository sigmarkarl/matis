package org.simmi;

import java.io.BufferedReader;
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

public class Simlab implements ScriptEngineFactory {
	Simple	engine = new Simple();
	
	static {
		System.loadLibrary( "simlab" );
	}
	
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
				BufferedReader br = new BufferedReader(reader);
				String line = br.readLine();
				while( line != null && !line.equalsIgnoreCase("quit") ) {
					if( line.equals("exit") ) System.exit(0);
					line = br.readLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
			return Simlab.this;
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
		
		Simlab  simlab = new Simlab();
		ScriptEngine engine = simlab.getScriptEngine();
		try {
			engine.eval( console.reader() );
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
		return null;
	}

	@Override
	public List<String> getMimeTypes() {
		return null;
	}

	@Override
	public List<String> getNames() {
		return null;
	}

	@Override
	public String getOutputStatement(String toDisplay) {
		return null;
	}

	@Override
	public Object getParameter(String key) {
		return null;
	}

	@Override
	public String getProgram(String... statements) {
		return null;
	}

	@Override
	public ScriptEngine getScriptEngine() {
		return engine;
	}

}
