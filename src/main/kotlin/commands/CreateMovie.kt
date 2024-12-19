package commands

import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import net.minestom.server.entity.Player

class CreateMovie : Command("createmovie") {
    init {
        setDefaultExecutor { sender, context ->
            if (sender !is Player) {
                sender.sendMessage("This command can only be run by players.")
            } else {
                sender.sendMessage("Usage: /createmovie <name>")
            }
        }

        val movieNameArgument = ArgumentType.String("movie_name")

        movieNameArgument.setCallback { sender: CommandSender, exception: ArgumentSyntaxException ->
            val input = exception.input
            sender.sendMessage("Your input was invalid.")
        }

        addSyntax({ sender: CommandSender, context: CommandContext ->
            val name = context.get(movieNameArgument)

        }, movieNameArgument)
    }
}
