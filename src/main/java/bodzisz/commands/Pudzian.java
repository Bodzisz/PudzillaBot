package bodzisz.commands;

import bodzisz.Pudzilla;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.util.EventListener;

public class Pudzian extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if(args[0].equalsIgnoreCase(Pudzilla.prefix + "pudzian")) {
            EmbedBuilder pudzian = new EmbedBuilder();
            pudzian.addField("PUDZIAN", "TANIO SKÓRY NIE SPRZEDAM", false);
            File file = new File("src/main/resources/pictures/pudzian1.jpg");
            pudzian.setImage("attachment://pudzian1.jpg");

            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage(pudzian.build()).addFile(file, "pudzian1.jpg").queue();
        }
    }
}
