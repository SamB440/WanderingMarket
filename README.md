# WanderingMarket
Item-based Minecraft global market plugin.

This takes advantage of the wandering villager and makes it appear to a random player that is online at a set interval. 

Players can then choose to add an item to the market in exchange for another item, by using `SHIFT+RIGHT-CLICK` and holding the item they wish to sell.

The wandering market will then go around player's who may or may not exchange their items.
This is a very useful plugin for SMPs without spawn commands and especially servers that like to keep bases secret.

## What about the vanilla wandering trader?
The vanilla wandering trader will have the same mechanics as usual, there are no changes to that.
Optionally, you can replace him with our market, and instead of using the interval it will just let vanilla handle that.

## What about dupes / OP items / etc?
Because of the way this works, you can't just magically get 64 obsidian or diamonds or similar. 
Another player on the server would need to have that, and deem that what they are exchanging it for is worth it.
There is also an item blacklist for stupid items like bedrock and air.

The economy will remain stable this way, even if you give 64 diamonds for one dirt, that is not inflating it in any way, that's just giving your diamonds away!
We also have dupe checks for things like bigger item stacks and invalid item exchange amounts, which you can optionally broadcast a message to shame that player if they try it.
