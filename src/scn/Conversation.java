package scn;

import java.util.ArrayList;
import java.util.HashMap;

import cls.game.Actor;
import run.Cache;
import run.Data;

public class Conversation extends Scene {
	
	private class CharMove {
		public final Actor actor;
		public final Position from;
		public final Position to;
		private double completion;
		private boolean hasFinished;
		
		private CharMove(Actor actor, Position from, Position to) {
			this.actor = actor;
			this.from = from;
			this.to = to;
			this.completion = 0;
			this.hasFinished = false;
		}
		
		private void update(double dt) {
			if (this.hasFinished) return;
			completion += dt * MOVE_SPEED;
			if (completion >= 1) {
				this.hasFinished = true;
			}
		}
		
		private void finish() {
			participants.put(actor, to);
			lineFinished = true;
			timer = SPEECH_DELAY;
		}
		
	}
	
	public static final int CONVERSATION_WIDTH = jog.Window.getWidth() / 2;
	public static final double SPEECH_DELAY = 1;
	public static final double MOVE_SPEED = 2;
	public static final double TYPING_SPEED = 0.1;
	
	private static final String SPEECH_PREFIX = "\"";
	private static final String MOVE_PREFIX = ">";

	public enum Position {
		OFFLEFT,
		LEFT,
		MIDLEFT,
		MIDRIGHT,
		RIGHT,
		OFFRIGHT,
	}
	
	private String name;
	private jog.Image backgroundImage;
	private jog.Audio.Source backgroundMusic;
	private String conversationFilename;
	private HashMap<Actor, Position> participants;
	private ArrayList<String> conversation;
	private double timer;
	private boolean lineFinished;
	private boolean charMoving;
	private CharMove charMovement;
	private int currentActionID;
	private String currentLine;
	private String currentText;
	private Position currentSpeaker;
	private boolean hasFinished;
	
	public Conversation(String conversation) {
		conversationFilename = conversation;
	}
	
	@Override
	public void start() {
		loadConversation(conversationFilename);
		timer = 0;
		currentActionID = -1;
		hasFinished = false;
		charMoving = false;
		nextLine();
	}

	public void movePerson(Actor actor, Position position) {
		participants.put(actor, position);
	}
	
	private void loadConversation(String convo) {
		participants = new HashMap<Actor, Position>();
		conversation = new ArrayList<String>();
		String[] levelData = jog.Filesystem.readFile(convo).split("\n");
		name = levelData[0];
		backgroundImage = Cache.conversationBackground(levelData[1]);
//		backgroundMusic = jog.Audio.newSource(levelData[2]); 
//		backgroundMusic.play();
		
		int i;
		for (i = 3; i < levelData.length && !levelData[i].isEmpty(); i ++) {
			setupPerson(levelData[i]);
		}
		i += 1; // Skip the blank line
		for (; i < levelData.length && !levelData[i].isEmpty(); i ++) {
			conversation.add(levelData[i]);
		}
	}

	private void setupPerson(String line) {
		String[] actorData = line.split(" "); 
		String actorName = actorData[0];
		Position position = Position.values()[Integer.parseInt(actorData[1])];
		cls.game.Actor actor = Data.getActor(actorName);
		movePerson(actor, position);
	}
	
	@Override
	public void update(double dt) {
		timer += dt;
		if (lineFinished) {
			if (timer >= SPEECH_DELAY) {
				timer -= SPEECH_DELAY;
				nextLine();
			}
		} else {
			if (charMoving) {
				charMovement.update(dt);
				if (charMovement.hasFinished) {
					charMovement.finish();
					charMovement = null;
					charMoving = false;
				}
			} else {
				while (timer >= TYPING_SPEED && !currentText.equals(currentLine)) {
					timer -= TYPING_SPEED;
					int i = currentText.length();
					currentText = currentLine.substring(0, i+1);
				}
				if (currentText.equals(currentLine)) {
					lineFinished = true;
					timer = 0;
				}
			}
		}
		if (hasFinished) {
			SceneManager.returnScene();
		}
	}
	
	public void nextLine() {
		currentActionID ++;
		if (currentActionID == conversation.size()) {
			hasFinished = true;
			return;
		}
		lineFinished = false;
		currentText = "";
		currentLine = "";
		String action = conversation.get(currentActionID); 
		if (action.startsWith(SPEECH_PREFIX)) {
			currentLine = action.split(" ", 2)[1];
			Actor actor = Data.getActor(action.substring(1).split(" ")[0]);
			currentSpeaker = participants.get(actor);
		} else if (action.startsWith(MOVE_PREFIX)) {
			Actor actor = Data.getActor(action.substring(1).split(" ")[0]);
			Position from = participants.get(actor);
			int positionTo = Integer.parseInt(action.split(" ")[1]);
			Position to = Position.values()[positionTo];
			charMovement = new CharMove(actor, from, to);
			charMoving = true;
		}
		// ~TODO: show the next line of conversation, do any moving and stuff.
		// prolly rename this function.
	}
	
	@Override
	public void keyPressed(int key) {
		if (!lineFinished) {
			lineFinished = true;
		} else {
			timer = SPEECH_DELAY;
		}
	}
	
	@Override
	public void draw() {
		jog.Graphics.draw(backgroundImage, 0, 0);
		jog.Graphics.rectangle(false, 0, 0, CONVERSATION_WIDTH, jog.Graphics.getHeight());
		drawPeople();
		drawSpeech();
		jog.Graphics.print(name, 0, 0);
	}
	
	private void drawPeople() {
		for (Actor a : participants.keySet()) {
			drawFace(a, participants.get(a));
		}
	}
	
	private void drawFace(Actor actor, Position position) {
		jog.Image face = Cache.actorFace(actor.getName());
		int n = Position.values().length - 2;
		int i = position.ordinal() - 1;
		int x = i * CONVERSATION_WIDTH / n;
		if (charMoving && charMovement.actor == actor) {
			int newX = (charMovement.to.ordinal() - 1) * CONVERSATION_WIDTH / n;
			int oldX = (charMovement.from.ordinal() - 1) * CONVERSATION_WIDTH / n;
			int dx = (int)((newX - oldX) * charMovement.completion);
			x += dx;
		}
		jog.Graphics.draw(face, x, jog.Window.getHeight() - face.getHeight());
	}
	
	private void drawSpeech() {
		drawSpeechBubble();
		if (lineFinished) {
			jog.Graphics.print(currentLine, 32, 256);
		} else {
			jog.Graphics.print(currentText, 32, 256);
		}
	}
	
	private void drawSpeechBubble() {
		jog.Graphics.roundedRectangle(false, 16, 240, 256, 64, 8);
		int y = 240 + 64;
		int x1 = (currentSpeaker.ordinal() - 1) * 192 / (Position.values().length - 2);
		x1 += (256 - 192);
		jog.Graphics.polygon(false, x1, y, x1 + 16, y, x1 + 8, y + 16);
	}
	
}
