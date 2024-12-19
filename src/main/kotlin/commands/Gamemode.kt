package commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import net.minestom.server.entity.Player

class Gamemode : Command("gamemode") {
    init {
        setDefaultExecutor { sender, context ->
            if (sender !is Player) {
                sender.sendMessage("This command can only be run by players.")
            } else {
                sender.sendMessage("Usage: /gamemode <creative|survival|spectator|adventure>")
            }
        }

        val gamemode = ArgumentType.String("gamemode")

        gamemode.setCallback { sender: CommandSender, exception: ArgumentSyntaxException ->
            val input = exception.input

            val textComponent = Component.text("Your gamemode was changed to ")
                .color(TextColor.color(0x443344))

            val player = sender as? Player
            if (player == null) {
                sender.sendMessage("You must be a player to use this command.")
                return@setCallback
            }

            when (input) {
                "creative" -> {
//                    player.setGameMode(net.minestom.server.entity.GameMode())
                }
            }
        }

        addSyntax({ sender: CommandSender, context: CommandContext ->
//            val name = context.get(movieNameArgument)
//
        }, gamemode)
    }
}
