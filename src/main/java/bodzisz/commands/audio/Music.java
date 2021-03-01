package bodzisz.commands.audio;

import bodzisz.Pudzilla;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.NoSuchElementException;

public class Music extends ListenerAdapter {

    private final AudioPlayerManager audioManager;
    GuildMusicManager guildMusicManager;
    Youtube yt;

    public Music() throws GeneralSecurityException, IOException {
        audioManager = new DefaultAudioPlayerManager();
        guildMusicManager = new GuildMusicManager(audioManager);
        AudioSourceManagers.registerRemoteSources(audioManager);
        AudioSourceManagers.registerLocalSource(audioManager);
        yt = new Youtube();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        String trackURL = "";
        if(args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                trackURL += args[i] + " ";
            }
        }
        else { trackURL = null; }


        if(args[0].equalsIgnoreCase(Pudzilla.prefix + "p") ||
                args[0].equalsIgnoreCase(Pudzilla.prefix + "play")) {

            play(trackURL,event, 1);
        }
        else if(args[0].equalsIgnoreCase(Pudzilla.prefix + "skip")) { skip(event); }
        else if(args[0].equalsIgnoreCase(Pudzilla.prefix + "leave")) { leave(event); }
        else if(args[0].equalsIgnoreCase(Pudzilla.prefix + "pause")) { pause(event); }
        else if(args[0].equalsIgnoreCase(Pudzilla.prefix + "np") ||
                args[0].equalsIgnoreCase(Pudzilla.prefix + "nowplaying")) { nowPlaying(event); }
        else if(args[0].equalsIgnoreCase(Pudzilla.prefix + "queue")) { showQueue(event); }
        else if(args[0].equalsIgnoreCase(Pudzilla.prefix + "pyt")) {
            try {
                trackURL = yt.getVideoId(trackURL);
            } catch (IOException e) {
               e.printStackTrace();
            }
            catch (NoSuchElementException e2) {
                trackURL = null;
            }
            play(trackURL,event, 2);
        }
        else if(args[0].equalsIgnoreCase(Pudzilla.prefix + "volume")) {
            if(args.length > 1) {
                volume(args[1], event);
            }
            else {
                volume(null, event);
            }
        }

        /*
        -p or -play + trackURL -> Starting playing music or unpausing
        -skip                  -> skips track
        -leave                 -> bot leaves the channel and queue is cleared
        -pause                 -> pauses current track
        -np or -nowplating     -> Sends message with current track title
        -queue                 -> Prints all the tracks in queue
        -pyt  + keywords       -> Play track from yt using keywords
        -volume + newValue[10,100] -> Change volume value
         */

    }

    private void play(String trackURL, GuildMessageReceivedEvent event, int type) {

            VoiceChannel channel = event.getMember().getVoiceState().getChannel();
            AudioManager guildAudioManager = channel.getGuild().getAudioManager();
            guildAudioManager.openAudioConnection(channel);
            guildAudioManager.setSendingHandler(guildMusicManager.getSendHandler());

            if(trackURL != null) {

                audioManager.loadItemOrdered(guildMusicManager, trackURL, new AudioLoadResultHandler() {
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
                        if(type == 2) {
                            event.getChannel().sendMessage("Couldn't find the song!").queue();
                        }
                        else {
                            event.getChannel().sendMessage("Queue is empty!").queue();
                        }
                    }
                }
                else {
                    guildMusicManager.player.setPaused(false);
                    if(type == 2) {
                        event.getChannel().sendMessage("Couldn't find the song!").queue();
                    }
                    else {
                        event.getChannel().sendMessage("Unpaused or already playing!").queue();
                    }
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
        String queue = "";
        queue += guildMusicManager.player.getPlayingTrack().getInfo().title + " (playing now)\n";
        for (AudioTrack a : guildMusicManager.scheduler.getQueue()) {
            queue += a.getInfo().title + "\n";
            if(queue.length() > 1850) {
                queue += "Couldn't load more, Queue is too long!";
                break;
            }
        }
        try {
            EmbedBuilder info = new EmbedBuilder();

            info.addField("Queue", queue, false);
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage(info.build()).queue();
        } catch (IllegalArgumentException e) {
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage(queue).queue();
        }
    }

    private void volume(String newVolume, GuildMessageReceivedEvent event) {
        int oldVolume = guildMusicManager.player.getVolume();
        if(newVolume == null) {
            event.getChannel().sendMessage("Current volume: " + oldVolume).queue();
        }
        else
        {
            try {
                int newVolumeInt = Math.max(10, Math.min(100, Integer.parseInt(newVolume)));
                guildMusicManager.player.setVolume(newVolumeInt);
                event.getChannel().sendMessage("Changed from " + oldVolume + " to " + newVolumeInt).queue();
            } catch (NumberFormatException e) {
                event.getChannel().sendMessage("Wrong input!").queue();
            }
        }
    }

}

