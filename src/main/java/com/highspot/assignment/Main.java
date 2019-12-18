package com.highspot.assignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.highspot.assignment.dto.action.ActionListDTO;
import com.highspot.assignment.dto.mixtape.MixtapeDTO;
import com.highspot.assignment.service.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "mixtape-exec", mixinStandardHelpOptions = true,
        version = "1.0-SNAPSHOT",
        subcommands = CommandLine.HelpCommand.class,
        description = "A command-line batch application that apply changes to a given mixtape json file",
        commandListHeading = "%nCommands:%n%nThe most commonly used git commands are:%n")
public class Main implements Callable<Integer> {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    @CommandLine.Option(names = "--input-file", description = "filepath of a json file contains users, songs and playlist.")
    private File inputFile;

    @CommandLine.Option(names = "--changes-file", description = "filepath of a json file contains changes for the input json file.")
    private File changesFile;

    @CommandLine.Option(names = "--output-file", description = "filepath for the output file after changes applied.")
    private File outputFile;

    @Override
    public Integer call() throws IOException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MixtapeDTO mixtapeDTO = objectMapper.readValue(inputFile, MixtapeDTO.class);
            ActionListDTO actionListDTO = objectMapper.readValue(changesFile, ActionListDTO.class);

            logger.info("input-file: \n" + objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(mixtapeDTO));

            logger.info("changes-file: \n" + objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(actionListDTO));

            MixtapeDTO result = SyncService.process(mixtapeDTO, actionListDTO);
            objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(outputFile, result);

            logger.info("output-file: \n" + objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(result));

            return 0;
        } catch (Exception e){
            logger.error("Failed to apply", e);
            Files.deleteIfExists(outputFile.toPath());
            return 1;
        }
    }

    public static void main(String... args) {
        System.exit(new CommandLine(new Main()).execute(args));
    }
}
