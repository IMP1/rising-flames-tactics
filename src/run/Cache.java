package run;

import java.util.HashMap;

public final class Cache {
	private Cache() {}
	
	private static HashMap<String, jog.Image> images = new HashMap<String, jog.Image>();
	
	public static jog.Image actorFace(String actorName) {
		String path = "gfx/face_" + actorName + ".png";
		if (!images.containsKey(path)) {
			 images.put(path, new jog.Image(path));
		}
		return images.get(path);
	}
	
	public static jog.Image conversationBackground(String backgroundName) {
		String path = "gfx/bg_" + backgroundName + ".png";
		if (!images.containsKey(path)) {
			 images.put(path, new jog.Image(path));
		}
		return images.get(path);
	}
	
}
