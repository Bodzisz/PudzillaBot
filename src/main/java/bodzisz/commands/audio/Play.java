package bodzisz.commands.audio;

import bodzisz.Pudzilla;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.File;

public class Play extends ListenerAdapter {

    private AudioPlayerManager audioManager;
    GuildMusicManager guildMusicManager;

    public Play() {
        audioManager = new DefaultAudioPlayerManager();
        guildMusicManager = new GuildMusicManager(audioManager);
        AudioSourceManagers.registerRemoteSources(audioManager);
        AudioSourceManagers.registerLocalSource(audioManager);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if(args[0].equalsIgnoreCase(Pudzilla.prefix + "p")) {

            VoiceChannel channel = event.getMember().getVoiceState().getChannel();
            AudioManager guildAudioManager = channel.getGuild().getAudioManager();
            guildAudioManager.openAudioConnection(channel);
            guildAudioManager.setSendingHandler(guildMusicManager.getSendHandler());


            if (args[1] != null) {

                audioManager.loadItemOrdered(guildMusicManager, args[1], new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack track) {
                        guildMusicManager.scheduler.queue(track);
                        event.getChannel().sendMessage("Added to queue: " + track.getInfo().title).queue();
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                        for (AudioTrack track : playlist.getTracks()) {
                            guildMusicManager.scheduler.queue(track);
                        }
                    }

                    @Override
                    public void noMatches() {
                        event.getChannel().sendMessage("No matches found!").queue();
                    }

                    @Override
                    public void loadFailed(FriendlyException throwable) {
                        event.getChannel().sendMessage("Load failed").queue();
                    }
                });
            }
        }
    }
}

