package me.htna.project.biomeanalyser.biomeanalyser.Commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

public class ReadImageListCommand extends BaseCommand {

    public final static String SUBPERMISSION = "readimagelist";
    public final static String[] ALIAS = {"readimagelist"};

    public ReadImageListCommand() {
        super(ALIAS, "이미지 목록을 가져옵니다.", SUBPERMISSION);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {


        return null;
    }
}
