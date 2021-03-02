package bodzisz.commands;

import bodzisz.Pudzilla;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Random;

public class Clear extends ListenerAdapter {

    final static Logger logger = LoggerFactory.getLogger(Clear.class);

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        EmbedBuilder clearInfo = new EmbedBuilder();
        try {
            String[] args = event.getMessage().getContentRaw().split("\\s+");

            if (args[0].equalsIgnoreCase(Pudzilla.prefix + "clear")) {
                int number;
                if(args.length < 2) {
                    number = 100;
                }
                else {
                    number = Integer.parseInt(args[1]);
                }

                List<Message> messages = event.getChannel().getHistory()
                        .retrievePast(number)
                        .complete();

                event.getChannel().deleteMessages(messages).queue();
                clearInfo.setTitle("✅ Deleted " + number + " messages!");
                event.getChannel().sendMessage(clearInfo.build()).queue();
            }
        } catch (InsufficientPermissionException e) {
            logger.info("Missing permissions: " + e.getMessage());
        } catch (IllegalArgumentException e2) {
            clearInfo.setTitle("❌ Can only delete between 2-100 messages");
            event.getChannel().sendMessage(clearInfo.build()).queue();
        }

    }
}
