package ru.spbau.mit.softwaredesign.cli.parser;

import org.junit.Test;
import ru.spbau.mit.softwaredesign.cli.errors.PipelineException;
import ru.spbau.mit.softwaredesign.cli.errors.UnknownExternalCommandException;
import ru.spbau.mit.softwaredesign.cli.pipe.BlockInfo;
import ru.spbau.mit.softwaredesign.cli.pipe.InputBuffer;
import ru.spbau.mit.softwaredesign.cli.pipe.OutputBuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Class that transforms tokens stream to blocks and executes these blocks consequently.
 * "Block" means part of the pipeline (e.g. tokens sublist separated by pipeline)
 * or the whole tokens list if pipeline is not used in the command line prompt.
 */
public class ChainExecutor {

    private List<List<String>> tokenizedCommandChain;
    private int numberOfCurrentBlock;
    private int countOfAllBlocks;

    /**
     * Construct chain of blocks.
     * @param tokens input tokens list
     */
    public ChainExecutor(List<String> tokens) {
        if (tokens.size() == 0)
            return;

        tokenizedCommandChain = new ArrayList<>();

        makeChain(tokens);
        countOfAllBlocks = tokenizedCommandChain.size();
        numberOfCurrentBlock = 0;
    }

    /**
     * Execute blocks, transfer buffer data from the previous block to the next one.
     * @return code that interprets result of chain commands execution built from tokens list {@see AbstractCommand, BlockExecutor}
     */
    public int execute() throws PipelineException, UnknownExternalCommandException {
        BlockExecutor blockExecutor = new BlockExecutor();
        for (List<String> nextBlock : tokenizedCommandChain) {

            BlockInfo blockInfo = new BlockInfo(nextBlock, numberOfCurrentBlock, countOfAllBlocks);

            OutputBuffer.redirectToInput();
            if (nextBlock.size() == 0) {
                throw new PipelineException();
            }
            if (blockExecutor.execute(blockInfo) == -1) {
                return -1;
            }
            InputBuffer.flush();
            numberOfCurrentBlock++;
        }
        OutputBuffer.print();
        return 0;
    }

    public static class TokenizedCommandChainTest {

        @Test
        public void chaining_separates_tokens_list_to_blocks_by_pipe_symbol() {
            List<String> tokens = Arrays.asList("1", " ", " ", "|", "2", "|", " ", "3", "|", " ", "4");
            ChainExecutor executor = new ChainExecutor(tokens);
            List<List<String>> blocks = executor.tokenizedCommandChain;
            assertEquals(4, blocks.size());
            assertEquals(3, blocks.get(0).size());
            assertEquals(1, blocks.get(1).size());
            assertEquals(2, blocks.get(2).size());
            assertEquals(2, blocks.get(3).size());
        }
    }

    /**
     * Split tokens list to blocks by pipeline symbol.
     * @param tokens input tokens list
     */
    private void makeChain(List<String> tokens) {
        List<String> currentBlock = new ArrayList<>();
        for (String token : tokens) {
            if (token.equals("|")) {
                tokenizedCommandChain.add(currentBlock);
                currentBlock = new ArrayList<>();
            } else {
                currentBlock.add(token);
            }
        }

        if (!currentBlock.isEmpty()) {
            tokenizedCommandChain.add(currentBlock);
        }
    }
}
