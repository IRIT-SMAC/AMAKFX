package fr.irit.smac.amak.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * Log tools
 * 
 * @author Alexandre Perles
 *
 */
public class Log {
	/**
	 * Comparable logging level
	 *
	 */
	public enum Level {
		/**
		* A fatal error should be thrown when the program is absolutely not able to continue
		*/
		FATAL(60),
		/**
		* An error which very probably causes alteration of the result
		*/
		ERROR(50),
		/**
		* An important log should be considered by the developer but doesn't have a real impact on the result
		*/
		IMPORTANT(40),
		/**
		* A warning doesn't have impact on the problem but should be considered for next updates
		*/
		WARNING(30),
		/**
		* Simply inform the developer or user
		*/
		INFORM(20),
		/**
		* Debug are the lowest level of logging. Unreadable data should be here
		*/
		DEBUG(10);

		/**
		 * The valud of importance of the log
		 */
		private final int order;

		/**
		 * Constructor of the log level
		 * @param order the order of importance (more means more important)
		 */
		Level(final int order) {
			this.order = order;
		}
		/**
		 * Check whether a log level is more important than another
		 * @param _other the log level to compare to
		 * @return true if this is more important than _other
		 */
		public boolean isGE(final Level _other) {
			return order >= _other.order;
		}
	}

	/**
	 * Is logging globally activated ?
	 */
	public static boolean enabled = true;
	/**
	 * Regexp aiming at filtering log lines with debug level by tag
	 */
	public static String debugTagFilter = "^.*$";
	/**
	 * Unique index for log lines
	 */
	private int idx = 1;
	/**
	 * The action that must be executed each time a log line is created
	 */
	private  List<Consumer<String>> actions = null;
	/**
	 * Default action to execute when no one is provided
	 */
	private static List<Consumer<String>> defaultAction = new ArrayList<Consumer<String>>();
	static {
		defaultAction.add((s) -> {
		System.out.println(s);
		System.out.flush();
		});
	}
	/**
	 * The default minimum level that should be displayed
	 */
	public static Level defaultMinLevel = Level.DEBUG;
	
	private Level minLevel = null;
	
	/**
	 * Global flag telling if the user has been informed about the fact that log
	 * shouldn't be disabled
	 */
	private static boolean alreadyInformed = false;
	
	/**
	 * The default logger. Use this logger for unspecific, global logging.
	 */
	public static Log defaultLog = new Log();
	
	private static HashMap<String, Log> loggers = new HashMap<String, Log>();
	
	/**
	 * Get or create a logger. <br/>
	 * A new logger copy the behavior of the default logger
	 * until told otherwise (when a callback is added for example). 
	 * @param name name of the logger
	 * @return
	 */
	public static Log get(String name) {
		if(!loggers.containsKey(name)) {
			loggers.put(name, new Log());
		}
		return loggers.get(name);
	}

	/**
	 * Log a line with log level set to debug
	 * 
	 * @param _tag
	 *            Tag of the line
	 * @param _text
	 *            Text of the log
	 * @param params
	 *            Parameters for the text (as in String.format)
	 */
	public void debug(final String _tag, final String _text, final Object... params) {
		log(Level.DEBUG, _tag, _text, params);
	}

	/**
	 * Log a line with log level set to error
	 * 
	 * @param _tag
	 *            Tag of the line
	 * @param _text
	 *            Text of the log
	 * @param params
	 *            Parameters for the text (as in String.format)
	 */
	public void error(final String _tag, final String _text, final Object... params) {
		log(Level.ERROR, _tag, _text, params);
	}

	/**
	 * Log a line with log level set to fatal
	 * 
	 * @param _tag
	 *            Tag of the line
	 * @param _text
	 *            Text of the log
	 * @param params
	 *            Parameters for the text (as in String.format)
	 */
	public void fatal(final String _tag, final String _text, final Object... params) {
		log(Level.FATAL, _tag, _text, params);
	}

	/**
	 * Get the action that must be executed for each log line
	 * 
	 * @return the action
	 */
	private List<Consumer<String>> getActions() {
		return actions == null ? defaultAction : actions;
	}

	/**
	 * Log a line with log level set to warning
	 * 
	 * @param _tag
	 *            Tag of the line
	 * @param _text
	 *            Text of the log
	 * @param params
	 *            Parameters for the text (as in String.format)
	 */
	public void important(final String _tag, final String _text, final Object... params) {
		log(Level.IMPORTANT, _tag, _text, params);
	}

	/**
	 * Log a line with log level set to inform
	 * 
	 * @param _tag
	 *            Tag of the line
	 * @param _text
	 *            Text of the log
	 * @param params
	 *            Parameters for the text (as in String.format)
	 */
	public void inform(final String _tag, final String _text, final Object... params) {
		log(Level.INFORM, _tag, _text, params);
	}

	/**
	 * Inform the user that logs shouldn't be disabled
	 */
	private void informOnce() {
		if (!alreadyInformed) {
			System.out.println("/!\\ Logs are disabled. Prefer the use of minimum level.");
			alreadyInformed = true;
		}
	}

	/**
	 * General logging method
	 * 
	 * @param _level
	 *            Level of log line
	 * @param _tag
	 *            Tag of the log line
	 * @param _text
	 *            Text of the log line
	 * @param params
	 *            Parameters for the text (as in String.format)
	 */
	private void log(final Level _level, final String _tag, final String _text, final Object... params) {
		if (enabled) {
			if (_level.isGE(getMinLevel())) {
				if (_level != Level.DEBUG || _tag.matches(debugTagFilter)) {
					getActions().forEach(
						item->item.accept(String.format("[%6s]	%6d/%s: %s", _tag, idx++, _level, String.format(_text, params)))
					);
				}
			}
		} else {
			informOnce();
		}
	}

	/**
	 * Add an action that will be used for logging lines. <br/>
	 * The logger will no longer use the default behavior.
	 * 
	 * @param _action
	 *            the action used for logging
	 */
	public void addCallback(final Consumer<String> _action) {
		if(actions == null) {
			actions = new ArrayList<Consumer<String>>();
		}
		actions.add(_action);
	}

	/**
	 * Log a line with log level set to warning
	 * 
	 * @param _tag
	 *            Tag of the line
	 * @param _text
	 *            Text of the log
	 * @param params
	 *            Parameters for the text (as in String.format)
	 */
	public void warning(final String _tag, final String _text, final Object... params) {
		log(Level.WARNING, _tag, _text, params);
	}
	
	/**
	 * Set the minimum level for this logger
	 * @param minLevel
	 */
	public void setMinLevel(Level minLevel) {
		this.minLevel = minLevel;
	}
	
	/**
	 * Get the minimum level for this logger. <br/>
	 * If the minimum level is lower than the default minimum level, return the default minimum level.
	 * @return
	 */
	public Level getMinLevel() {
		if(minLevel == null) {
			return defaultMinLevel;
		} else {
			return minLevel.isGE(defaultMinLevel) ? minLevel : defaultMinLevel;
		}
	}
	
	public boolean isDefaultActions() {
		return actions == null;
	}

}
