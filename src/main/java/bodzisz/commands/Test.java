package bodzisz.commands;

import bodzisz.Pudzilla;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.text.MessageFormat;

public class Test extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if(args[0].equalsIgnoreCase(Pudzilla.prefix + "test")) {
            EmbedBuilder info = new EmbedBuilder();
            info.setTitle("CHUJ");
            info.addField("New Field", "TANIO SKÓRY NIE SPRZEDAM", false);
            info.setDescription("Testing if everything works");
            info.setColor(0xb30000);

            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage(info.build()).queue();
        }

    }



}
