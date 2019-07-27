GUI DESIGN DOCUMENT
===

The NightGamesMod GUI consists of 5 screens: 
* dialog
* match
* daytime
* shop
* closet

This is a living document, and only some of these screens have been designed.  Accordingly, they 
will not all appear below.

The Dialog Screen
===

The dialog screen encompasses a variety of uses involving interactions between characters.  Some
examples are detailed below:

* Visits with another character
* Combat is a dialog between combatants.
* Watching combat is a dialog between two characters where neither is the player.
* The Pre-match can be considered a dialog with Lily

The following are *not* a dialog:
* Shops

```
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* 2             * 1                                 * 3             *
*               *                                   *               *
*               *                                   *               *
*               *                                   *               *
*               *                                   *               *
*               *                                   *               *
*               *                                   *               *
*               *                                   *               *
*               *                                   *               *
*               *                                   *               *
*               *                                   *               *
*               *                                   *               *
*               *                                   *               *
*               *                                   *               *
*               *                                   *               *
*               *                                   *               *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* 4                                                                 *
*                                                                   *
*                                                                   *
*                                                                   *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```
1. Story Panel
1. Partner Panel
1. Player Panel
1. Command Panel

Story Panel
---

The story panel is the main panel in the center of the screen where events and dialog are written 
out.  Any messages sent to the UI while it is in Dialog mode are readable in the story panel.  This 
includes everything from a description of combat actions, narration, and dialog between characters.

```
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*                                                                 ^ *
*   * * * * * * * * * * * * * * * * * * * * * * * * * * * * *       *
*     *      Lorem ipsum dolor sit amet consectetur         *       *
*       *    adipiscing elit, sed nulla eget cum class      *       *
*         *  varius pellentesque, maecenas posuere dictum   *       *
*         *  pharetra ad iaculis.                         1 *       *
*         * * * * * * * * * * * * * * * * * * * * * * * * * *       *
*                                                                   *
*   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *   *
*   * Lacinia ultrices himenaeos donec quam cubilia aliquet, ad *   *
*   * malesuada nam eros integer, montes sem quisque duis       *   *
*   * tristique. Lacus nunc condimentum taciti dis vulputate    *   *
*   * congue metus ullamcorper, interdum ac penatibus odio nam  *   *
*   * purus hendrerit sed etiam, nostra est quis pulvinar quam  *   *
*   * nullam tempor.                                          2 *   *
*   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *   *
*                                                                   *
*   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *   *
*   * Cassie uses her hands to    * You use your hands to       *   *
*   * slap you across the face,   * massage Cassie's breasts    *   *
*   * leaving a stinging heat on  * over her T-shirt.           *   *
*   * your cheek.                 * Cassie was pleasured for 8  *   *
*   * You take 8 stamina damage.  * arousal                     *   *
*   * Cassie builds 8 mojo.       *                           3 *   *
*   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *   *
*                                                                 v *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```

There are three kinds of story elements that can appear in the Story Panel:
* Dialog: Used anything that has a speaker. The carat points to the speaker.
* Narration: Used for describing actions taken by characters in a exclusive/non-adversarial context.
* Adversarial Narration: Primarily used for combat actions, but any time two characters take an 
action on each other simultaneously qualifies.

The story panel is used to record the story the player is writing with the NPCs. We want the player 
to be able to go back and read their story, if they want, once they are finished writing it.  For 
that reason, the story panel is scrollable.  New elements appear on the bottom as if writing new 
words on a page. Left untouched, they will eventually scroll off the top of the pane. For usability,
the story panel will scroll all the way back down to the bottom with each new message.

Partner Panel
---

The partner panel is a tall panel that dominates the right portion of the screen. This panel 
displays information about the object of the player's dialog.  If watching combat, the partner and 
"player" can be selected at random.  In that case, both will use the partner panel layout, since 
the player doesn't have inside knowledge of either character.

```
* * * * * * * * * * * * * * * * * * * * * * * * *
* 1                  CASSIE                     *
* * * * * * * * * * * * * * * * * * * * * * * * *
* 2                                             *
*                                               *
*                                               *
*                                               *
*                                               *
*                                               *
*                                               *
*                                               *
*                                               *
*                                               *
*                                               *
*                                               *
*                                               *
*                                               *
*                                               *
* * * * * * * * * * * * * * * * * * * * * * * * *
* 3                  TIRED                      *
*                                               *
*                   AROUSED                     *
*                                               *
*                   FOCUSED                     *
*                                               *
*                STRONG WILLED                  *
*                                               *
* * * * * * * * * * * * * * * * * * * * * * * * *
* 4                                             *
*                   * * * *                     *
*                   *     *                     *
*                   *     *                     *
*                   * * * *                     *
*         * * * *   * * * *   * * * *           *
*         *     *   *     *   *     *           *
*         *     *   *     *   *     *           *
*         * * * *   * * * *   * * * *           *
*         * * * *   * * * *   * * * *           *
*         *     *   *     *   *     *           *
*         *     *   *     *   *     *           *
*         * * * *   * * * *   * * * *           *
*                   * * * *                     *
*                   *     *                     *
*                   *     *                     *
*                   * * * *                     *
*                                               *
* * * * * * * * * * * * * * * * * * * * * * * * *
```

The partner panel is divided into four pieces:
1. The panel header contains the partner's name.
1. Occupying the top third of the panel, the partner's portrait is displayed here.  If clicked, 
it's replaced with a text description of the partner.
1. The remainder of the top half is for the opponent's status.  This uses descriptors to display 
their stamina, arousal, mojo, and willpower.
1. The bottom half of the panel is dedicated to their visible clothing.  Clicking it change it to a 
screen with effect cards describing all of the partner's ongoing visible effects.

The idea behind the partner panel is that the player only has access to information about their 
partner that there senses can perceive.  The visible clothing section, for example only displays 
the _top layer_ of clothing the character is wearing.  We want players to be able to notice if, for 
example, the character is wearing the opera gloves and gaining enhanced slaps, to encourage them to 
remove the gloves. Hovering over an inventory item will a display a tooltip with it's effects. 
The same ethos carries over to the effect cards and status descriptions.

The portraits are a beloved part of NightGamesMod, and so we present them in the dominant top-left 
corner of the screen. However, because real estate is at a premium, they're forced to share space 
with the other representation of a character's appearance, the generated text description. This 
seems a reasonable enough compromise given the similar role played by these two objects.

Player Panel
---

```
* * * * * * * * * * * * * * * * * * * * * * * * *
* 1              BOND, JAMES BOND               *
* * * * * * * * * * * * * * * * * * * * * * * * *
* 2                                             *
*                                               *
*                                               *
*                                               *
*                                               *
*                                               *
*                                               *
*                                               *
*                                               *
*                                               *
*                                               *
*                                               *
*                                               *
* * * * * * * * * * * * * * * * * * * * * * * * *
* 3         50/100                        TIRED *
* STAMINA   =================------------------ *
*           80/160                      AROUSED *
* AROUSAL   =================------------------ *
*           60/100                      FOCUSED *
* MOJO      ======================------------- *
*           40/40                 STRONG WILLED *
* WILLPOWER =================================== *
* * * * * * * * * * * * * * * * * * * * * * * * *
* 4                                             *
*                   * * * *                     *
*                   *     *                     *
*                   *     *                     *
*                   * * * *                     *
*         * * * *   * * * *   * * * *           *
*         *     *   *     *   *     *           *
*         *     *   *     *   *     *           *
*         * * * *   * * * *   * * * *           *
*         * * * *   * * * *   * * * *           *
*         *     *   *     *   *     *           *
*         *     *   *     *   *     *           *
*         * * * *   * * * *   * * * *           *
*                   * * * *                     *
*                   *     *                     *
*                   *     *                     *
*                   * * * *                     *
*                                               *
* * * * * * * * * * * * * * * * * * * * * * * * *
```
The player panel is, similarly to the partner panel, broken up into four pieces:
1. The header for the panel is the player's chosen name.
1. The picture panel here will be used for any combat images that get displayed. This isn't
strictly player related, but it does relate to the player's status. That's close enough for 
government work.  If clicked, it will switch to the generated text description of the player.
1. The exposes the same data as the status panel in the partner panel and so much more.  It's main
purpose is to provide information to the player about their primary status.  Additionally, it 
provides a context cue for interpreting the partner panel's status descriptions.  Since the colours 
and status text is the same, players will learn to interpret their partner's status in terms of 
their own, avoiding using real estate to explain it.
1. Similarly, the inventory panel exposes the player's visible inventory. The design on this element
is admittedly lower priority and so I haven't worked out all the details of display. I'd like to 
have a way to show clothing hidden under other layers.  If clicked, it cycles to a group of effect 
cards for the player's active effects (unlike the partner panel, visible and invisible).

The Player panel, fittingly, is where information is displayed about the player character.  As it
also displays information about a character, it looks very similar to the partner panel.  However, 
the Player panel goes into much more depth about what is happening to the player's character.

Command Panel
---

Choices for the player to make are presented on the command panel. The design problem to be solved
in this panel is to avoid overwhelming the player with options, while still allowing them to quickly
find what they're looking for.  It occupies the bottom portion of the screen.

```
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* 1                                         / \                                           *
*                                          /   \                                          *
*     * * * * * *   * * * * * *     /   * * * * * *   \     * * * * * *   * * * * * *     *
*     * Fondle  *   *  Lick   *    /    * Finger  *    \    *  Tease  *   *  Taunt  *     *
*     * Breasts *   * Nipples *    \    *   her   *    /    *         *   *         *     *
*     * * * * * *   * * * * * *     \   * * * * * *   /     * * * * * *   * * * * * *     *         
*                                                                                         *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* 2                                      Foreplay                                         *   
*                                                                                         *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
```
1. The selection panel lays out the options in the currently selected category.  These may be things
that have an effect on the game world itself, or they could be categories.
1. The category panel both displays the player's currently engaged category and acts as a "back"
button to return to the previous selection group.

Selections on the command panel form a tree of choices.  Some are leaves, or "commands", that have
direct effect on the game world.  Others are nodes, or "categories", which branch into more
categories and commands. The categories need not be homogeneous.

The selection panel will scroll horizontally from side to side to display all the possible
selections.  These can be controlled with the keyboard or the mouse, by clicking the buttons on any
side of the selected option. The panel will keep itself balanced to present a consistent visual.
If at any point there are undisplayed options, and room on one side of the panel or the other due to
player scrolling, the furthest undisplayed option will be placed on the side of the panel with 
space.  The panel will be adaptive, and hold more options as there the window gets bigger. If there
are fewer than 3 options (selected and one on each side), the existing options will be duplicated so
that the player always has a clear idea of what option will be selected when they shift to one side
or the other.

The category panel is a button (which will have a keyboard shortcut) to go back up the tree.  It's 
not visible if the player is at the root of the tree.  When a category is selected, it will shift 
down and expand to indicate the selection of that category.  That category's options will then be
presented in the selection panel.