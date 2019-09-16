package fr.irit.smac.amak.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * An interface to easily logs info from a class to its dedicated file. 
 * @author Hugo
 *
 */
public interface Loggable {
	
	/**
	 * Return a logger for this class. <br/>
	 * Will create a dedicated logs file for this class. If the logs file already exist, it is overwritten. <br/>
	 * The default file has the fully qualified name of the class, and is located at the app root.
	 * @return
	 */
	default public Log logger() {
		Log log = Log.get(this.getClass().getName()+".log");
		if(log.isDefaultActions()) {
			deleteLogs();
			log.addCallback((s) -> {
				try (FileWriter fw = new FileWriter(this.getClass().getName()+".log", true)){
					fw.write(s+"\n");
					fw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
		return log;
	}
	
	/**
	 * Delete the files for the logger of this class.
	 */
	default public void deleteLogs() {
		File f = new File(this.getClass().getName()+".log");
		if(f.exists())
			f.delete();
	}
}
