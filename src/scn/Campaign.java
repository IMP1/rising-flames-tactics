package scn;

public class Campaign extends Scene {
	
	private static final String BATTLE_PREFIX = "!";
	private static final String CONVERSATION_PREFIX = "\"";
	
	private String[] levels;
	private int currentLevelID;
	private String campaignFilename;
	
	public Campaign(String campaign) {
		campaignFilename = campaign;
	}
	
	@Override
	public void start() {
		levels = jog.Filesystem.readFile(campaignFilename).split("\n");
		currentLevelID = -1;
		nextScene();
	}
	
	private void nextScene() {
		currentLevelID ++;
		if (currentLevelID >= levels.length) {
			SceneManager.returnScene();
			return;
		}
		if (isBattle()) {
			SceneManager.addScene(new scn.Battle(getBattleFilename()));
		} else if (isConversation()) {
			SceneManager.addScene(new scn.Conversation(getConversationFilename()));
		}
	}
	
	@Override
	public void update(double dt) {
		nextScene();
	}
	
	private boolean isBattle() {
		return levels[currentLevelID].startsWith(BATTLE_PREFIX);
	}
	
	private boolean isConversation() {
		return levels[currentLevelID].startsWith(CONVERSATION_PREFIX);
	}
	
	private String getBattleFilename() {
		return getFilename(BATTLE_PREFIX);
	}
	
	private String getConversationFilename() {
		return getFilename(CONVERSATION_PREFIX);
	}
	
	private String getFilename(String prefix) {
		return levels[currentLevelID].split(prefix)[1];
	}

}
