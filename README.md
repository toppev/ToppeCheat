## ToppeCheat (a.k.a ToppeSkid)
A discontinued server-side anticheat developed in ~2016.

I decided to publish this even though it's nothing compared to modern anticheats. This anticheat was developed for a big Finnish server and was also used by a few other servers. It had some decent checks (that worked well back in 2015-2017). The performance of this plugin was quite poor mainly due to the lack of asynchronous routines.

Not the cleanest code because I was quite new to Java and programming.

### Some Features
- Movement cheat detection (flying, speeding)
- Combat cheat detection (multiple checks for killaura, aimbot, health regeneration, criticals and bow without cooldown)
- Unusual clicking pattern detection (autoclickers, macros, double clickers, triggerbots and more)
- Alert online staff on violation
- Ban players automatically
- Automatic chat filter and punishments
- Log violations, punishments and other data to flat-file database or SQL database and inspect later. Easily create a pastebin dump with one command
- Admin commands to manage players and checks

### Installation
**Note:** Only works with 1.8.8 spigot. You can add support for other versions though.
1. Clone this repository `git clone https://github.com/toppev/ToppeCheat` or `git clone git@github.com:toppev/ToppeCheat.git`
2. Do your changes and when you're done compile it with `mvn clean package`
3. Drop the jar in your server's `plugins` folder and start the server

**Note:** You most likely want to tweak the settings in settings.yml configuration or/and add your own checks
