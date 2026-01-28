package com.hytale.ctf.commands;

import com.hytale.api.command.Command;
import com.hytale.api.command.CommandSender;
import com.hytale.ctf.arena.ArenaManager;
import com.hytale.ctf.flag.FlagManager;
import com.hytale.ctf.game.CTFGame;
import com.hytale.ctf.player.PlayerManager;
import com.hytale.ctf.team.TeamManager;

/**
 * Adapter to integrate CTFCommand with Hytale Command API.
 * Bridges between Hytale's command system and our internal command handlers.
 */
public class CTFCommandAdapter implements Command {
    
    private final CTFCommand ctfCommand;
    
    public CTFCommandAdapter(
        ArenaManager arenaManager,
        FlagManager flagManager,
        PlayerManager playerManager,
        TeamManager teamManager,
        CTFGame game,
        com.hytale.ctf.storage.DataManager dataManager
    ) {
        this.ctfCommand = new CTFCommand(
            arenaManager,
            flagManager,
            playerManager,
            teamManager,
            game,
            dataManager
        );
    }
    
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        return ctfCommand.execute(sender, args);
    }
    
    @Override
    public String getName() {
        return "ctf";
    }
    
    @Override
    public String getDescription() {
        return "Main CTF command for Capture the Flag minigame";
    }
    
    @Override
    public String getUsage() {
        return "/ctf <subcommand> [args...]";
    }
    
    @Override
    public String getPermission() {
        return "ctf.use";
    }
}
