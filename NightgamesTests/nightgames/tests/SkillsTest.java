package nightgames.tests;

import java.util.List;

import nightgames.characters.BasePersonality;

import nightgames.areas.Area;
import nightgames.stance.Position;

public class SkillsTest {
	List<BasePersonality> npcs1;
	List<BasePersonality> npcs2;
	List<Position> stances;
	Area area;
	/*
	@Before
	public void prepare() throws JsonParseException, IOException {

		Global.initForTesting();

		Global.newGame("Dummy", Optional.empty(), Collections.emptyList(),
	                    CharacterSex.male, Collections.emptyMap());
		npcs1 = new ArrayList<Personality>();
		npcs2 = new ArrayList<Personality>();
		try {
			ClassLoader classLoader = this.getClass().getClassLoader();
			System.out.println(classLoader.getResource(""));
			npcs1.add(new CustomNPC(JsonSourceNPCDataLoader.load(SkillsTest.class.getResourceAsStream("hermtestnpc.js"))));
			npcs1.add(new CustomNPC(JsonSourceNPCDataLoader.load(SkillsTest.class.getResourceAsStream("femaletestnpc.js"))));
			npcs1.add(new CustomNPC(JsonSourceNPCDataLoader.load(SkillsTest.class.getResourceAsStream("maletestnpc.js"))));
			npcs1.add(new CustomNPC(JsonSourceNPCDataLoader.load(SkillsTest.class.getResourceAsStream("asextestnpc.js"))));
			// don't set fake human right now because there are a lot of casts being done
			//npcs1.forEach(npc -> npc.getCharacter().setFakeHuman(true));

			npcs2.add(new CustomNPC(JsonSourceNPCDataLoader.load(SkillsTest.class.getResourceAsStream("hermtestnpc.js"))));
			npcs2.add(new CustomNPC(JsonSourceNPCDataLoader.load(SkillsTest.class.getResourceAsStream("femaletestnpc.js"))));
			npcs2.add(new CustomNPC(JsonSourceNPCDataLoader.load(SkillsTest.class.getResourceAsStream("maletestnpc.js"))));
			npcs2.add(new CustomNPC(JsonSourceNPCDataLoader.load(SkillsTest.class.getResourceAsStream("asextestnpc.js"))));
		} catch (JsonParseException e) {
			e.printStackTrace();
			Assert.fail();
		}
		area = new Area("Test Area","Area for testing", Movement.quad);
		stances = new ArrayList<Position>();
		stances.add(new Anal(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new AnalCowgirl(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new AnalProne(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new Behind(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new BehindFootjob(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter(), false));

        stances.add(new BehindFootjob(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter(), true));

		stances.add(new CoiledSex(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new Cowgirl(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new Doggy(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new Engulfed(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new FaceSitting(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new FlowerSex(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new FlyingCarry(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new FlyingCowgirl(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new HeldOral(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new Jumped(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new Missionary(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new Mount(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new Neutral(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new NursingHold(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new Pin(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new ReverseCowgirl(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new ReverseMount(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new SixNine(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new Standing(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new StandingOver(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new TribadismStance(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
		stances.add(new UpsideDownFemdom(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
        stances.add(new UpsideDownMaledom(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
        stances.add(new HeldOral(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
        stances.add(new HeldPaizuri(npcs1.get(0).getCharacter(), npcs1.get(1).getCharacter()));
	}

	public void testSkill(Character npc1, Character npc2, Position pos) throws CloneNotSupportedException {
		Combat c = new Combat(npc1, npc2, area, pos);
		pos.checkOngoing(c);
		if (c.getStance() == pos) {
			for (Skill skill : Global.getSkillPool()) {
				Combat cloned = c.clone();
				Skill used = skill.copy(cloned.p1);
				if (Skill.isUsable(cloned, used)) {
					System.out.println("["+cloned.getStance().getClass().getSimpleName()+"] Skill usable: " + used.getLabel(cloned) + ".");
					used.resolve(cloned, cloned.p2);
				}
			}
		} else {
			System.out.println("STANCE NOT EFFECTIVE: " + pos.getClass().getSimpleName() + " with top: " + pos.top.getTrueName() + " and bottom: " + pos.bottom.getTrueName());
		}
	}

	// TODO: May need to clone npc1 and npc2 here too, depending on how skills affect characters.
	public void testCombo(Character npc1, Character npc2, Position pos) throws CloneNotSupportedException {
		pos.top = npc1;
		pos.bottom = npc2;
		testSkill(npc1, npc2, pos);
		testSkill(npc2, npc1, pos);
	}

	@Test
	public void test() throws CloneNotSupportedException {
		for (int i = 0; i < npcs1.size(); i++) {
			for (int j = 0; j < npcs2.size(); j++) {
				System.out.println("i = " + i + ", j = " + j);
				for (Position pos : stances) {
					NPC npc1 = npcs1.get(i).getCharacter();
					NPC npc2 = npcs2.get(j).getCharacter();
					System.out.println("Testing [" + i + "]: " + npc1.getTrueName() + " with [" + j + "]: " + npc2.getTrueName() + " in Stance " + pos.getClass().getSimpleName());
					testCombo(npc1.clone(), npc2.clone(), pos);
					System.out.println("Testing [" + j + "]: " + npc2.getTrueName() + " with [" + i + "]: " + npc1.getTrueName() + " in Stance " + pos.getClass().getSimpleName());
					testCombo(npc2.clone(), npc1.clone(), pos);
				}
			}
		}
		System.out.println("test " + Global.random(100000) + " done");
	}
	*/
}
