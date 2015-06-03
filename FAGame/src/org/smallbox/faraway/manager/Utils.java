package org.smallbox.faraway.manager;

import java.io.File;

public class Utils {

	private static int _uuid;

	public static int getUUID() {
		return ++_uuid;
	}

	public static void useUUID(int usedId) {
		if (_uuid < usedId + 1) {
			_uuid = usedId + 1;
		}
	}

	public static long getLastUIModified() {
		long lastModified = 0;

		for (File file: new File("data/ui/").listFiles()) {
			if (file.isDirectory()) {
				for (File subFile : file.listFiles()) {
					if (subFile.lastModified() > lastModified) {
						lastModified = subFile.lastModified();
					}
				}
			}
			if (file.lastModified() > lastModified) {
				lastModified = file.lastModified();
			}
		}

		for (File file: new File("data/strings/").listFiles()) {
			if (file.lastModified() > lastModified) {
				lastModified = file.lastModified();
			}
		}

		return lastModified;
	}
}
