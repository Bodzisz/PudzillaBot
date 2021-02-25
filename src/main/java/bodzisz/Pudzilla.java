package bodzisz;

import bodzisz.commands.Test;
import bodzisz.commands.Pudzian;
import bodzisz.events.GuildMemberJoin;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;


public class Pudzilla {
    public static JDA jda;
    public final static String token = "ODEzODE4NTAxODM5MDYxMDAy.YDU1ig.TktzbXtSFK8ch2jsbl_DaI59004";
    final static Logger logger = LoggerFactory.getLogger(Pudzilla.class);
    public static final String prefix = "-";

    public static void main(String[] args) throws LoginException {
        jda = JDABuilder.createDefault(token).enableIntents(GatewayIntent.GUILD_MEMBERS).build();

        jda.addEventListener(new Test());
        jda.addEventListener(new GuildMemberJoin());
        jda.addEventListener(new Pudzian());

    }
}
