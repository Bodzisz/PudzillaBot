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

public class Music extends ListenerAdapter {

    private final AudioPlayerManager audioManager;
    GuildMusicManager guildMusicManager;

    public Music() {
        audioManager = new DefaultAudioPlayerManager();
        guildMusicManager = new GuildMusicManager(audioManager);
        AudioSourceManagers.registerRemoteSources(audioManager);
        AudioSourceManagers.registerLocalSource(audioManager);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if(args[0].equalsIgnoreCase(Pudzilla.prefix + "p") ||
                args[0].equalsIgnoreCase(Pudzilla.prefix + "play")) { play(args,event); }
        else if(args[0].equalsIgnoreCase(Pudzilla.prefix + "skip")) { skip(event); }
        else if(args[0].equalsIgnoreCase(Pudzilla.prefix + "leave")) { leave(event); }
        else if(args[0].equalsIgnoreCase(Pudzilla.prefix + "pause")) { pause(event); }
        else if(args[0].equalsIgnoreCase(Pudzilla.prefix + "np") ||
                args[0].equalsIgnoreCase(Pudzilla.prefix + "nowplaying")) { nowPlaying(event); }
        else if(args[0].equalsIgnoreCase(Pudzilla.prefix + "queue")) { showQueue(event); }

        /*
        -p or -play + trackURL -> Starting playing music or unpausing
        -skip                  -> skips track
        -leave                 -> bot leaves the channel and queue is cleared
        -pause                 -> pauses current track
        -np or -nowplating     -> Sends message with current track title
        -queue                 -> Prints all the tracks in queue
         */

    }

    private void play(String[] args, GuildMessageReceivedEvent event) {

            VoiceChannel channel = event.getMember().getVoiceState().getChannel();
            AudioManager guildAudioManager = channel.getGuild().getAudioManager();
            guildAudioManager.openAudioConnection(channel);
            guildAudioManager.setSendingHandler(guildMusicManager.getSendHandler());

            if (args.length > 1) {

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
            else {
                if(guildMusicManager.player.getPlayingTrack() == null) {
                    if (!guildMusicManager.scheduler.isEmpty()) {
                        if (guildMusicManager.player.isPaused()) {
                            guildMusicManager.player.setPaused(false);
                        }
                    } else {
                        event.getChannel().sendMessage("Queue is empty!").queue();
                    }
                }
                else {
                    guildMusicManager.player.setPaused(false);
                    event.getChannel().sendMessage("Unpaused or already playing!").queue();
                }
            }
    }

    private void skip(GuildMessageReceivedEvent event) {
        if(!guildMusicManager.scheduler.isEmpty()) {
            guildMusicManager.scheduler.nextTrack();
            event.getChannel().sendMessage("Skipped!").queue();
        }
        else {
            event.getChannel().sendMessage("Queue is empty!").queue();
        }
    }

    private void leave(GuildMessageReceivedEvent event) {
        event.getGuild().getAudioManager().setSendingHandler(null);
        event.getGuild().getAudioManager().closeAudioConnection();
        guildMusicManager.scheduler.clearQueue();
        guildMusicManager.scheduler.nextTrack();
    }

    private void pause(GuildMessageReceivedEvent event) {
        guildMusicManager.player.setPaused(true);
        event.getChannel().sendMessage("Music paused!").queue();
    }

    private void nowPlaying(GuildMessageReceivedEvent event) {
        if(guildMusicManager.player.getPlayingTrack() != null) {
            event.getChannel().sendMessage("Playing " + guildMusicManager.player.getPlayingTrack().getInfo().title).queue();
        }
        else {
            event.getChannel().sendMessage("Nothing is being played at the moment!").queue();
        }
    }

    private void showQueue(GuildMessageReceivedEvent event) {
        EmbedBuilder info = new EmbedBuilder();
        String queue = "";
        queue += guildMusicManager.player.getPlayingTrack().getInfo().title + " (playing now)\n";
        for(AudioTrack a : guildMusicManager.scheduler.getQueue()) {
            queue += a.getInfo().title + "\n";
        }
        info.addField("Queue", queue, false);
        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessage(info.build()).queue();
    }

}

