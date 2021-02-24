package bodzisz.events;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GuildMemberJoin extends ListenerAdapter {

    String[] messages = {
            "[member] pewnie tanio skory nie sprzeda",
            "POLSKA GUROM!!! [member] przyszedl",
            "Nawet to ze [member] przyszedl nic nie dalo",
            "[member] wjechal jak low king Pudziana w piszczel Najmana"
    };

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Random random = new Random();
        int number = random.nextInt(messages.length);

        Objects.requireNonNull(event.getGuild().getDefaultChannel()).sendMessage(messages[number].
                replace("[member]", event.getMember().getAsMention())).queue();

    }
}
