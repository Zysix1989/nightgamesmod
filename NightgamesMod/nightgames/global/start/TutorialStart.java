package nightgames.global.start;

import nightgames.global.Global;
import nightgames.global.Scene;
import nightgames.gui.GUI;
import nightgames.match.MatchType;
import nightgames.modifier.standard.NoModifier;

import java.util.Arrays;

public class TutorialStart implements GameStarter, Scene {
    private MatchType matchType;

    @Override
    public void respond(String response) {
        Global.current = this;
        switch (response) {
            case "Start":
                Global.gui().prompt("Would you like to play the tutorial sequence?",
                    Arrays.asList(GUI.sceneOption("Yes"), GUI.sceneOption("No")));
                break;
            case "Yes":
                Global.gui()
                      .prompt(fmt("<i>\"So, we've got a group full of newbies,\"</i> Maya says. <i>\"I"
                                      + " think it would be a good idea to go over the rules in a bit"
                                      + " more detail, and to give you all some practical tips. Since"
                                      + " you will be fighting one-on-one - at least for now - you "
                                      + "should pair up.\"</i> You glance over to Cassie, and she seems to"
                                      + " have the same idea you do. The two of you quickly stand "
                                      + "together, while Mara and Jewel do the same. Angel pairs with"
                                      + " Maya, for now. <i>\"Good. We will be changing up the pairs "
                                      + "later on, but for now, let's get started. As I've explained,"
                                      + " to win, you are going to have to bring your opponent to "
                                      + "orgasm. Obviously, since sexfights are still sex, you don't"
                                      + " get to skip to the best part straight away; you need to"
                                      + " get them and yourself worked up a little, first. So that's"
                                      + " where we are going to begin. Get yourselves ready.\"</i>"),
                          Arrays.asList(GUI.sceneOption("Get Ready")));
                break;
            case "Get Ready":
                Global.gui()
                      .prompt(fmt("You face Cassie who, like you, is still a little"
                                      + " red in the face. To your left, Jewel lunges at Mara,"
                                      + " getting behind her and roughly groping her breasts "
                                      + "before Mara worms away. This is going to be pretty intense, "
                                      + "you figure. You look back at Cassie and begin walking "
                                      + "towards her. As soon as you are close enough, you charge,"
                                      + " but Cassie sidesteps you and tickles your sides as you"
                                      + " pass. You barely manage to keep your balance, but soon"
                                      + " you recover and tickle her right back. "),
                          Arrays.asList(GUI.sceneOption("Tickle")));
                break;
            case "Tickle":
                Global.gui()
                      .prompt(fmt("Things soon develop into a full-blown ticklefight until Maya "
                                      + "comes along and puts a stop to it. You both sit down, breathing "
                                      + "heavily after the exertion. <i>\"Good, you're getting in the mood. "
                                      + "It's important that you feel at ease with the situation, and "
                                      + "confident in yourself and your abilities. Things like this help "
                                      + "where those are concerned; ticking, kissing, some light petting,"
                                      + " they are all good for building some confidence. Or, 'building "
                                      + "some mojo', as the kids call it these days. Once you're a bit "
                                      + "more at ease you can start trying some more adventureous things."
                                      + " Which brings us to the next portion of the proceedings.\"</i>"),
                          Arrays.asList(GUI.sceneOption("Continue")));
                break;
            case "Continue":
                Global.gui()
                      .prompt(fmt("Maya reforms the pairings, and you are now standing in"
                                      + " front of Jewel, while Angel and Mara are facing off to the"
                                      + " side. <i>\"As you will have noticed,\"</i> Maya begins, <i>\"all of"
                                      + " you are wearing clothes. And as I'm sure you are all aware,"
                                      + " your partner wearing clothes is counter-productive when you"
                                      + " are trying to give them pleasure. So at some point, you are"
                                      + " going to have to get your opponent naked. Now that you've"
                                      + " all warmed up some, you can try getting some clothes off"
                              + " your opponent. Go!\"</i> "),
                          Arrays.asList(GUI.sceneOption("Strip Bottoms")));
                break;
            case "Strip Bottoms":
                Global.gui()
                      .prompt(fmt("Jewel didn't hesitate last time, and you doubt she will now, "
                                      + "so you quickly take the initiative and run towards her, reaching"
                                      + " for her jeans. You try to yank these down her legs, but she "
                                      + "easily steps away from you and pulls her jeans back up into "
                                      + "position. She then immediately follows up by tugging your shirt"
                                      + " off of you in a single, rapid motion. <i>\"Good job, Jewel! As "
                                      + "you've both learned, taking someone's pants of while they are "
                                      + "standing is pretty difficult. Shirts are easier, but it still "
                                      + "takes some doing. You'll have a much better chance if you, for "
                                      + "example, distract your opponent first.\"</i> Getting the hint, you"
                                      + " stealthily move behind Jewel as Maya drones on."),
                          Arrays.asList(GUI.sceneOption("Strip Top")));
                break;
            case "Strip Top":
                Global.gui()
                      .prompt(fmt("You quickly pull the straps of her tank top over her shoulders and pull"
                                      + " it down to her waist. <i>\"{self:name}!\"</i> she yells, but she soon remembers"
                                      + " where you both are and smiles. <i>\"Nice job, {self:name}. It won't work "
                                      + "again.\"</i> Jewel lets the top fall down to her feet and steps out of it,"
                                      + " jiggling her bra-clad breasts on front of you. Off to the side, Mara "
                                      + "and Angel have been grappling back and forth a bit, neither gaining a"
                                      + " real advantage yet. Suddenly, however, Mara gains a good hold on "
                                      + "Angel's shirt and then tries to sweep her feet out from under her. "
                                      + "Angel recovers before she falls, but the distraction is enough for "
                                      + "Mara to completely remove Angel's shirt. Angel, it seems, does not "
                                      + "make a habit of wearing bras, as her glorious, nude chest is exposed"
                                      + " for all to see. Angel catches you staring and grins broadly, "
                                      + "spreading her arms to give you a better view.Maya has to physically"
                                      + " shake you to break your stare. <i>\"Seriously, {self:name}? It's like"
                                      + " you've never seen tits before! {self:if-male:And I know that isn't"
                                      + " true. None of you were selected without consulting some... </i>"
                                      + "references<i>}{self:if-female:Which would be very surprising, "
                                      + "considering the fact you've got a fine pair of them yourself}. Anyway,"
                                      + " now that we are all back on Earth, let's proceed, shall we?\"</i>"),
                          Arrays.asList(GUI.sceneOption("Positioning")));
                break;
            case "Positioning":
                Global.gui()
                      .prompt(fmt("Maya matches you up with Mara now, Cassie and Angel"
                                      + " form the second pair. <i>\"Now, tickling and groping is all "
                                      + "well and good, but that's not what any of us are here for."
                                      + " We've already seen that getting into a good position is "
                                      + "important for getting your opponent's clothes off, but of"
                                      + " course there are many more things you can do once you "
                                      + "have free access to your opponent's body. No one is going"
                                      + " to just lay down for you to ravage, of course. If you "
                                      + "want to get on top, you'll have to fight for it. You'll"
                                      + " want to make your opponent a little weary first, so they"
                                      + " won't be able to fight you off when you move in for the"
                                      + " kill. As {self:name} and Cassie so expertly demonstrated"
                                      + " before, tickling is one way to accomplish this. If you"
                                      + " want, you can also be a bit less nice. Slaps, knees, "
                                      + "kicks and most other things are allowed, as long as you "
                                      + "don't cause injury.\"</i> Jewel grins as she hears this and "
                                      + "cracks her kunckles in anticipation before Maya continues."
                                      + " <i>\"Ahem. As I was saying, once you think your opponent is "
                                      + "sufficiently softened up, give them a good shove. We'll "
                                      + "continue once two of you are on the ground.\"</i> She expects "
                                      + "you to actually hurt Mara? That doesn't sound very nice..."
                                      + " As you ponder this, Mara slaps you hard across the cheek."
                                      + " <i>\"Come on, {self:name}! You signed up for this!\"</i> Well, "
                                      + "that's true. You and Mara exchange slaps, punches to the "
                                      + "stomach and the occasional tickle for a while, and you are"
                                      + " both getting out of breath. Then Mara draws back, panting"
                                      + ". How do you react?"),
                          Arrays.asList(GUI.sceneOption("Slap"), GUI.sceneOption("Tackle")));
                break;
            case "Slap":
                Global.gui()
                      .prompt(fmt("She must be nearing the edge! You step in, reaching out to "
                                      + "deliver the final blow, but as you do so she sidesteps you and "
                                      + "takes your legs out from underneath you. You land flat on your "
                                      + "back and the impact knocks the wind out of you. Before you can "
                                      + "recover, Mara settles herself down on your stomach, grinning "
                                      + "confidently. <i>\"And stop!\"</i> Maya calls. Cassie is in a similar"
                                      + " position to you, with Angel atop her belly and those luscious"
                                      + " tits of hers in her face. At least you don't have that "
                                      + "distraction. Mara looks down at you confidently, <i>\"I think "
                                      + "I like it up here, {self:name}. If we meet during the real match"
                                      + " tonight, I'm going to make sure we revisit this.\"</i>"),
                          Arrays.asList(GUI.sceneOption("In Trouble")));
                break;
            case "Tackle":
                Global.gui()
                      .prompt(fmt("Mara's a crafty one; she must be planning something. You don't"
                                      + " want to give her any opportunity to spring anything on you, so you "
                                      + "charge in and tackle her to the ground. She gasps as she hits the "
                                      + "ground, giving you an opening to sit down on her waist. Not wanting"
                                      + " to miss the opportunity, you quickly get rid of her shirt. <i>\"Hey! "
                                      + "That wasn't part of the exercise! Give it back!\"</i> Fortunately, Maya"
                                      + " steps in. <i>\"No, don't. You can't put clothing back on once it's "
                                      + "been taken off. Them's the rules. Okay, now that both bouts are "
                                      + "finished, we can move on.\"</i> Looking to the side, sure enough, Angel"
                                      + " is dangling her sumptuous busom in front of Cassie's face as she "
                                      + "sits atop her, pinning her hands over her head"),
                          Arrays.asList(GUI.sceneOption("On Top")));
                break;
            case "On Top":
                Global.gui()
                      .prompt(fmt("<i>\"So, {self:name} and Angel have the advantage now. That means "
                                      + "Mara and Cassie need to do something to escape. Unfortunately for them,"
                                      + " their options are a little limited. You can try to exhaust your opponent, "
                                      + "just push them off, or slip out of their hold. If you're really clever "
                                      + "about it, you might be able to roll over and turn the tables on them,"
                                      + " but I don't think any of you have that much experience, yet. Meanwhile,"
                                      + " of course, {self:name} and Angel get to do whatever they like. Let's"
                                      + " go!\"</i> Whatever you want? Well, she doesn't need to ask twice. You "
                                      + "reach under Mara's back and unclasp her bra. She has moved her hands "
                                      + "down to her pants, so you lift them up over her head and pull the bra "
                                      + "off. You see a flash of black as you move her arms, but think nothing "
                                      + "of it. Mara wiggles her torso a bit, causing her breasts to jiggle. "
                                      + "<i>\"Come on then, show me what you've got!\"</i> Mara seems to have forgotten"
                                      + " the point of this 'fight', but that is no reason for you not to take"
                                      + " her up on her challenge. You reach out and grab a breast in each hand,"
                                      + " massaging them gently and then moving up to her nipples, giving them"
                                      + " light tweaks. As you rub the two mounds together, Mara arms suddenly "
                                      + "come down and lock a thick black zip tie around your wrists! Taking full"
                                      + " advantage of your surprise, she manages to roll you over, placing you "
                                      + "in the same position you were just in, but with your hands tied. Cassie,"
                                      + " too, has managed to escape, though not in such a grand fashion as Mara"
                                      + " did. Maya seems quite happy with her performance. <i>\"Excellent job, Mara!"
                                      + " I think you're going to do just fine in these games. {self:name}, there's"
                                      + " not much you can do with your hands tied, so you will have to get that"
                                      + " zip tie off. Meanwhile, of course, Mara can do as she pleases to you.\"</i>"),
                          Arrays.asList(GUI.sceneOption("Bound")));
                break;
            case "In Trouble":
                Global.gui()
                      .prompt(fmt("<i>\"Okay, {self:name} and Cassie are in a bit of trouble right now. "
                                      + "In their current situation, their options are limited and their opponents"
                                      + " can do pretty much whatever they like. To escape, they could try to "
                                      + "exhaust their opponent, shove them off, or just squirm out from underneath"
                                      + " them. Let's see what they try, shall we?\"</i>"),
                          Arrays.asList(GUI.sceneOption("Escape")));
                break;
            case "Escape":
                Global.gui()
                      .prompt(fmt("Mara runs her hands over your naked chest, paying special attention "
                                      + "to your nipples. <i>\"Oh, yes. I really am going to enjoy these nights.\"</i> It"
                                      + " feels good, but you can't just let her get the upper hand. Surprising "
                                      + "her with a sudden move, you roll over onto your stomach and start to "
                                      + "crawl away. Mara was briefly dislodged, but easily catches up to you and "
                                      + "grabs your arms. She pulls them behind your back and ties your wrists "
                                      + "together with a zip tie! You try to escape, but Mara flips you over and"
                                      + " sits back down. That escape attempt did not accomplish much... Maya"
                                      + " comes along to rub some salt into your wounded pride. <i>\"Almost, but not"
                                      + " quite, {self:name}! You actually managed to be worse off than before."
                                      + " You've probably already discovered that having your hands tied is a "
                                      + "very bad thing. You're going to have to get that tie off before you can"
                                      + " fight back. Mara, of course, does not have to just sit back and watch"
                                      + " you escape. She gets to do what she wants to you.\"</i>"),
                          Arrays.asList(GUI.sceneOption("Bound")));
                break;
            case "Bound":
                Global.gui()
                      .prompt(fmt("As you start to wriggle your wrists, pulling on the thick plastic "
                                      + "band, Mara moves down to your feet. It's not much of a surprise when"
                                      + " she hooks her fingers under your jeans and underwear and pulls it "
                                      + "all off in one go, exposing your {self:main-genitals} to all those "
                                      + "assembled. <i>\"Looking good, {self:name}!\"</i> Angel chimes in from the"
                                      + " sideline. Having an audience for this kind of thing is going to "
                                      + "take some getting used to. Mara doesn't give you a chance to ponder"
                                      + " this, as she starts petting and fondling your {self:main-genitals},"
                                      + " quietly murmering something to herself."),
                          Arrays.asList(GUI.sceneOption("Struggle")));
                break;
            case "Struggle":
                Global.gui()
                      .prompt(fmt("Mara's hands continue their work, {self:if-male:fondling your"
                                      + " still-limp dick and gently squeezing its head, trying to get "
                                      + "you hard.}{self:if-female:petting your mound and slipping a finger"
                                      + " between your labia, spreading out the small bit of lubrication "
                                      + "which is starting to leak from you.} You get more serious about "
                                      + "your struggling, and eventually manage to snap the zip tie. With "
                                      + "Mara now sitting on your feet, it's much easier for you to get "
                                      + "away from her. Within seconds you are both standing, the only "
                                      + "difference being that you are now completely naked. <i>\"Okay, now "
                                      + "that {self:name} is no longer a poor helpless naked {self:boy},"
                                      + " just a naked one, we can continue.\"</i>"),
                          Arrays.asList(GUI.sceneOption("On we go")));
                break;
            case "On we go":
                Global.gui()
                      .prompt(fmt("Maya changes up the pairs again, and you will now be "
                                      + "'practicing' with Angel. Her and those glorious tits, that is. "
                                      + "It's quite a chore to pull your eyes away and focus on Maya. "
                                      + "Angel notices your fascination and winks seductively at you. "
                                      + "<i>\"Ahem, {self:name}, you with us?\"</i> Maya acts annoyed, but you"
                                      + " hear thinly veiled amusement in her voice. <i>\"Good, as I was "
                                      + "saying, you will be affected by any number of things in many ways"
                                      + " during a fight. Maybe your opponent pulls a clever trick to"
                                      + " distract you, or you find some inner focus and can suddenly"
                                      + " concentrate better. Then there are the more exotic things, "
                                      + "but you will have to discover those for yourselves. Angel and "
                                      + "{self:name}, are you up for a little demonstration?\"</i> It's not"
                                      + " like you can refuse, is it? Maya pulls Angel to the side and "
                                      + "whispers in her ear. If Angel's wide grin is any indication, "
                                      + "she likes whatever Maya is suggesting. Having given her "
                                      + "instructions to Angel, Maya speaks up again. <i>\"Okay {self:name},"
                                      + " here's the challenge. When I say go, you will wait for ten "
                                      + "seconds, and then slap Angel as hard as you can. Angel is not"
                                      + " allowed to touch you, move away from you, or block your "
                                      + "attack in any way. If you succeed, you can have your undies "
                                      + "back.\"</i> Well that sounds easy enough. You and Angel face each"
                                      + " other, and Maya gives the signal to start. Angel turns around"
                                      + " while swaying sinuously. It clicks in your mind when she "
                                      + "starts slowly pulling her skirt and underwear off. For some "
                                      + "reason she's giving you a strip tease! And a good one at that."
                                      + " You are utterly mesmerized by the look of her shapely bottom"
                                      + " right until she turns around. You don't know where to look, and"
                                      + " your eyes fly across her body, from those great breasts you "
                                      + "just can't get enough of to her slim belly and the naked slit "
                                      + "beneath. Remembering the challenge, somehow, you step in close "
                                      + "and draw back a hand in preparation, but you just can't bring "
                                      + "yourself to go through with it. Maya breaks your stupor. <i>\"Time!"
                                      + " Angel wins! She charmed {self:name} to the point where "
                                      + "{self:pronoun} just couldn't bear to hurt her. It's going to take"
                                      + " some practice for her to do this in the heat of the moment, but"
                                      + " I am sure she will manage it. The rest of you can learn to do "
                                      + "the same thing, of course, and this is only one of many ways in which"
                                      + " you can debilitate your opponent. Okay, we're almost done. Just "
                                      + "two things left to explain.\"</i>"),
                          Arrays.asList(GUI.sceneOption("The first thing")));
                break;
            case "The first thing":
                Global.gui()
                      .prompt(fmt("<i>\"I should probably take this opportunity to remind you all "
                                      + "of the confidentality agreement you signed. We have access to some "
                                      + "things that do not exist as far as the public is concerned, and we"
                                      + " would like to keep it that way. Most of this stuff, you won't be "
                                      + "seeing for a while. Some of it is for sale - if you know where to "
                                      + "buy it - but most is restricted to higher-ranked fighters. Still, "
                                      + "just to give you a taste, I brought some samples. Let's start with "
                                      + "this one.\"</i> She takes a flask out of her handbag and hands it over "
                                      + "to you. It's filled with a pinkish liquid, but there are no identifying "
                                      + "markers of any kind. <i>\"Some of the goodies you might come across "
                                      + "during your tenure at the Games can get pretty weird, and they might"
                                      + " have drastic, long-term consequences. These samples are more benign"
                                      + " ones, though; nothing to be afraid of. Right, Angel? Would you take"
                                      + " one for the team, please?\"</i> Angel hesitates for a few moments - "
                                      + "wisely, you figure - but then agrees. <i>\"Great! Okay, {self:name}, just"
                                      + " throw some of that stuff onto her breasts, and we'll observe the results.\"</i>"),
                          Arrays.asList(GUI.sceneOption("Throw Draft")));
                break;
            case "Throw Draft":
                Global.gui()
                      .prompt(fmt("You uncork the flask and pitch some of the pinkish stuff at Angel."
                                      + " It would be difficult to miss such a target, and indeed the splash of"
                                      + " mystery fluids lands across her chest. It's absorbed unnaturally "
                                      + "quickly into her skin, and her eyes widen as something begins to happen."
                                      + " Her breasts - which were plenty impressive already - swell up an "
                                      + "additional cup size. Angel runs her hands over her expaned bust and "
                                      + "gasps at the sensation. <i>\"As Angel just found out, this draft, which"
                                      + " we call Bust Draft because we're not the most creative bunch, causes "
                                      + "breasts to grow both in size and sensitivity. I will leave the possible"
                                      + " use cases up to your perverted minds. Oh and yes, it does work on men"
                                      + " as well.\"</i> {self:if-male:She gives you a meaningful wink. You'll "
                                      + "probably want to evade that stuff, you figure.} Suddenly, a splash of "
                                      + "wetness hits your back. You turn around to see Mara holding a flask "
                                      + "filled with a cloudy blue liquid, smiling at you.<i> \"I just wanted to"
                                      + " see what this one does,\"</i> she says, <i>\"For science, you know?\"</i>. Maya"
                                      + " giggles. <i>\"Oh, this is going to be good!\"</i>"),
                          Arrays.asList(GUI.sceneOption(Global.getPlayer()
                                                                          .hasDick() ? "Oh, wow!" : "Oh, shit!")));
                break;
            case "Oh, wow!":
                Global.gui()
                      .prompt(fmt("You feel a tingling in your dick, intensifying with each passing moment."
                                      + " When you look down, you see it grow longer and thicker, even though"
                                      + " you are somehow still flaccid. Angel wolf-whistles at you, and the"
                                      + " rest of the girls also seem quite appreciative. Not that you were "
                                      + "going to complain, of course. Maya is the only one who does not give"
                                      + " more than a quick glance down at your elongated member. Not her "
                                      + "preference, perhaps? <i>\"Well, {self:name}, it was pretty good before,"
                                      + " but if the others' expressions are anything to go by I'd say you "
                                      + "just got about an inch and a half more popular around here. Sorry to"
                                      + " disappoint, but it will go back to normal in a little while. For "
                                      + "now, though, what do you say we give it a test run?\"</i> Uhh. Duh."),
                          Arrays.asList(GUI.sceneOption("Sexytimes")));
                break;
            case "Oh, shit!":
                Global.gui()
                      .prompt(fmt("An intense tingling starts in your clit. You freeze in shock when you see a penis -"
                                      + " an actual, no-nonsense penis! - sprout from your nethers. The other girls,"
                                      + " with the obvious exception of Maya, are equally gobsmacked. Very tentatively,"
                                      + " you reach down and touch your new appendage. You've held cocks before, but"
                                      + " holding your own is something else entirely. With the shock of the whole "
                                      + "situation, it's no surprise that it hangs limp between your legs, but it "
                                      + "still seems a decent enough size. You try to ask a hundred questions at "
                                      + "once of Maya, but only end up muttering nonsense. <i>\"Like I said, pretty "
                                      + "crazy stuff. Don't worry, it will wear of in ten minutes or so. Just enough"
                                      + " time to take it for a test ride, don't you figure?\"</i> You feel extremely "
                                      + "uncomfortable with the whole situation. But... You are also curious what "
                                      + "it would feel like, so you agree."),
                          Arrays.asList(GUI.sceneOption("Sexytimes")));
                break;
            case "Sexytimes":
                Global.gui()
                      .prompt(fmt("<i>\"What we've done so far is foreplay, which while certainly a lot of fun, is often "
                                      + "not the final destination of a sexual foray. There are some complications "
                                      + "where penetrative sex is concerned, though, so I figured we had best go over"
                                      + " some. Just to state the painfully obvious to begin with, limp dicks and "
                                      + "dry pussies are not going to accomplish anything. As you gain some "
                                      + "experience, you may discover a way to always be ready for sex, but until and"
                                      + " unless you do so, you are going to need some stimulation first. The same "
                                      + "goes for your partner, of course. Now, during any good sex you'll be focused"
                                      + " on your partner's pleasure, and even more so in a competitive setting like"
                                      + " this. Just keep in mind that being on top does not mean you don't feel "
                                      + "anything yourself; don't get overconfident. As a final tip before we move "
                                      + "on to our practical demonstration, remember that even if you are not in "
                                      + "control of the sex, you can still do plenty from the bottom to give your "
                                      + "opponent a hard time. You could use your hands or mouth, and even just "
                                      + "struggling will cause *ahem* friction. Alright, then, Angel and {self:name},"
                                      + " up here please.\"</i>"),
                          Arrays.asList(GUI.sceneOption(Global.getPlayer()
                                                                          .hasDick() ? "Let's Go!"
                                                                                          : "Here goes nothing...")));
                break;
            case "Let's Go!":
                Global.gui()
                      .prompt(fmt("Well, this is going to be different. Not only are you about to fuck a stunning girl"
                                      + " who you just met an hour ago - you were not quite <i>that</i> popular "
                                      + "before - but you'll have an audience looking over your shoulder. Those "
                                      + "irregularities are easily offset by the look of Angel laying down in front "
                                      + "of you on her back and opening her legs to you. Your dick is definitely "
                          + "starting to respond, now. "), Arrays.asList(GUI.sceneOption("Fuck")));
                break;
            case "Here goes nothing...":
                Global.gui()
                      .prompt(fmt("Well, this is going to be different. You're still getting used to the odd weight hanging "
                                      + "between your legs, and now you're supposed to use it somehow? A part of you is "
                                      + "intrigued to see how it's going to feel from this end, the other part is terrified "
                                      + "at the whole situation. This is not what you had in mind when you woke up this "
                                      + "morning. The curiosity wins out eventually, and you take a deep breath to steel "
                          + "yourself."), Arrays.asList(GUI.sceneOption("Mount")));
                break;
            case "Mount":
                Global.gui()
                      .prompt(fmt("You kneel down in front of Angel and lower your head to her thighs. From there, you trace "
                                      + "a line of kisses past her moist pussy, then up her stomach to her nippers, finally "
                                      + "following the curve of her neck to her mouth. She embraces you as you sink into a "
                                      + "deep kiss, and you move a hand down to play with her folds. {self:if-female:You are"
                                      + " in no hurry to move on; this is the part you're good at.} Angel purrs in appreciation"
                                      + ", and reciprocates in kind, fondling your hardening shaft. Under her skillful"
                                      + " touch, it's not long before you have grown completely hard. {self:if-female:It"
                                      + " feels... odd. Not like you had expected at all, yet weirdly good. A pressure"
                                      + " emanates from the rigid shaft, and you notice an instinctual need to stick it"
                          + " somewhere.}"), Arrays.asList(GUI.sceneOption("Fuck")));
                break;
            case "Fuck":
                Global.gui()
                      .prompt(fmt("Feeling that Angel is also ready, you break the kiss and shift your position."
                                      + " Looking into her eyes, you push in. She's very tight and warm, and you "
                                      + "already know you won't last five minutes like this. {self:if-female:Your"
                                      + " mind is awash with the strange new sensations, but while you can't really"
                                      + " think straight some primal part of you is ordering your hips to start "
                                      + "thrusting.}{self:if-male:Well, just have to make sure she comes before that.} "),
                          Arrays.asList(GUI.sceneOption("Thrust")));
                break;
            case "Thrust":
                Global.gui()
                      .prompt(fmt("You settle into a comfortable pace, alternating kisses on her lips with ones in "
                                      + "her neck. Angel, meanwhile tweaks both of your nipples, which sends an "
                                      + "unexpected shock down to your cock. Her hips push back against you as she"
                                      + " moans. <i>\"Mmmm, {self:if-female:either that potion instilled some really"
                                      + " good instincts, or you have a natural gift}{self:if-male:I don't know how"
                                      + " good you were before that strange potion}, {self:name}, but I could "
                                      + "certainly get used to this. But this isn't all you've got, is it?\"</i>"),
                          Arrays.asList(GUI.sceneOption("Piston")));
                break;
            case "Piston":
                Global.gui()
                      .prompt(fmt("You are not about to back away from such a challenge, and increase your pace."
                                      + " You start playing with her nipples, and she responds by moving her hands"
                                      + " down to your ass, encouraging your thrusts. Angel's moans have grown"
                                      + " louder, and a couple of grunts escape your throat as well. You don't "
                                      + "know how close Angel is, but you're certainly getting there. Going for "
                                      + "broke, you start pounding Angel as fast as you can, eliciting a yelp of"
                                      + " surprise among the cacophony of pleasure surrounding you both. She does"
                                      + " seem to be getting close now, but so are you. You close your eyes in "
                                      + "concentration, hoping to gain a few seconds to outlast her. Suddenly, "
                                      + "she exploits your singular focus and rolls you over, now riding you in "
                                      + "the cowgirl position with the same passion you were giving her. It's too"
                                      + " much for you, and within seconds you explode into her. She is only moments"
                                      + " behind you, screaming in ecstasy. Her undulating canal milks your "
                                      + "spasming dick, drawing out every bit of your sperm. After a short while,"
                                      + " she collapses on top of you, then rolls of to the side. The other girls"
                                      + " step in to surround your panting bodies, looking down at you from above, "
                                      + "all clearly flustered with arousal. <i>\"{self:name},\"</i> Angel says between "
                                      + "her heavy breathing, <i>\"we are going to be doing that a lot more. And it's"
                                      + " going to end with you losing every time.\"</i> You agree with the first half"
                                      + " of her assertion, but don't respond."),
                          Arrays.asList(GUI.sceneOption("Recover")));
                break;
            case "Recover":
                Global.gui()
                      .prompt(fmt("You both get to your feet, and Maya presents each of you with a can. <i>\"Don't worry."
                                      + " Nothing transformative in these. They are just energy drinks. Well, they "
                                      + "</i>might<i> contain something to make sure that {self:if-male:those of us"
                                      + " endowed with equipment which might ordinarily not be up to repeated use "
                                      + "can go all night.}{self:if-female:you all recover from a fight's exertions"
                                      + " in record speed.} But I'm sure no one has a problem with that, right?\"</i> "
                                      + "Shrugging mentally, you open the can and chug down the caffeinated contents"
                                      + " in one go. That... was pretty awful. Angel seems to share your opinion, "
                                      + "seeing how she has contorted her face into an expression which could almost"
                                      + " - almost - be construed as rather charmless. With a sudden, somewhat "
                                      + "queasy feeling, {self:if-female:your temporary dick draws back into your"
                                      + " body, leaving only smooth flesh behind. <i>\"Aww,\"</i> Angel pouts, <i>\"I was "
                                      + "starting to like him!\"</i>}{self:if-male:your dick shrinks back to its"
                                      + " original, though still respectable size, earning a few strange looks "
                          + "from the girls.}"), Arrays.asList(GUI.sceneOption("Awww")));
                break;
            case "Awww":
                Global.gui()
                      .prompt(fmt("<i>\"A fine effort from everyone! There is much more to learn, to experience, and to"
                                      + " enjoy, but you will have to find out all of that for yourselves. If this "
                                      + "little practice session is any indication, I'm sure the rest of the "
                                      + "organization and I will be very well pleased with your participation."
                                      + " Now then, you've got your starting positions. Go there, and your first real"
                                      + " match will start at ten. Have fun, and good luck!\"</i> The girls and you get"
                                      + " your clothes back in order and exchange some final pleasantries before "
                                      + "leaving. Somehow, it seems as if you've gotten yourself in <i>way</i> over"
                                      + " your head. Will it work out, or be a complete disaster? Only time will tell."),
                          Arrays.asList(GUI.sceneOption("Done")));
                break;
            case "Done":
            case "No":
                assert matchType != null;
                Global.gui().clearText();
                Global.setUpMatch(matchType, new NoModifier());
                break;
            default:
                assert matchType != null;
                Global.gui().message("<b>Error: Invalid Tutorial Scene '" + response + "', starting game.</b>");
                Global.setUpMatch(matchType, new NoModifier());
        }
    }

    private String fmt(String format, Object... args) {
        return Global.format(format, Global.getPlayer(), Global.noneCharacter(), args);
    }

    @Override
    public void startGame(MatchType type) {
        this.matchType = type;
        respond("Start");
    }

}
