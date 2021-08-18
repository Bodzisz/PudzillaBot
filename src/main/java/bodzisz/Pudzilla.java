package bodzisz;

import bodzisz.Token.FileParser;
import bodzisz.commands.Clear;
import bodzisz.commands.Test;
import bodzisz.commands.Pudzian;
import bodzisz.commands.audio.Music;
import bodzisz.commands.audio.Youtube;
import bodzisz.events.GuildMemberJoin;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.security.GeneralSecurityException;


public class Pudzilla {
    public static JDA jda;
    final static Logger logger = LoggerFactory.getLogger(Pudzilla.class);
    public static final String prefix = "-";

    public static void main(String[] args) throws Exception {
        final String token = FileParser.readFileAsString("src/main/resources/token.txt");;
        jda = JDABuilder.createDefault(token).enableIntents(GatewayIntent.GUILD_MEMBERS).build();

        jda.addEventListener(new Test());
        jda.addEventListener(new GuildMemberJoin());
        jda.addEventListener(new Pudzian());
        jda.addEventListener(new Music());
        jda.addEventListener(new Clear());
    }
}
