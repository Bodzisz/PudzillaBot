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

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if(args[0].equalsIgnoreCase(Pudzilla.prefix + "p")) {
            AudioManager guildAudioManager = event.getGuild().getAudioManager();
            AudioPlayerManager audioManager = new DefaultAudioPlayerManager();
            AudioSourceManagers.registerRemoteSources(audioManager);
            AudioPlayer player = audioManager.createPlayer();
            AudioPlayerSendHandler audioPlayerSendHandler = new AudioPlayerSendHandler(player);
            guildAudioManager.setSendingHandler(audioPlayerSendHandler);

            TrackScheduler trackScheduler = new TrackScheduler(player);
            player.addListener(trackScheduler);

            try {
                VoiceChannel channel = event.getMember().getVoiceState().getChannel();
                guildAudioManager.openAudioConnection(channel);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            audioManager.loadItem("https://www.youtube.com/watch?v=GaeXlnNf2R8&ab_channel=%23GM2LTV", new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    trackScheduler.queue(track);
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    for (AudioTrack track : playlist.getTracks()) {
                        trackScheduler.queue(track);
                    }
                }

                @Override
                public void noMatches() {
                    // Notify the user that we've got nothing
                }

                @Override
                public void loadFailed(FriendlyException throwable) {
                    // Notify the user that everything exploded
                }
            });

            player.playTrack(player.getPlayingTrack());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            event.getChannel().sendMessage(player.getPlayingTrack().getInfo().title).queue();
        }
    }


}
