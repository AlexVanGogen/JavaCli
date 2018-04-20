package ru.spbau.mit.softwaredesign.cli.pipe;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.List;

/**
 * Class that contains information of separate block of the pipeline:
 * ordered number of block, count of all blocks in the pipeline,
 * list of tokens that block consists of.
 */
public class BlockInfo {

    private List<String> tokensList;
    private int numberOfCurrentBlock;
    private int countOfAllBlocks;

    public BlockInfo(List<String> tokensList, int numberOfCurrentBlock, int countOfAllBlocks) {
        this.tokensList = tokensList;
        this.numberOfCurrentBlock = numberOfCurrentBlock;
        this.countOfAllBlocks = countOfAllBlocks;
    }

    /**
     * Get position of block in the pipeline
     * @return relative position of block
     */
    public SpecificPosition getRelativePosition() {
        if (countOfAllBlocks == 1) {
            return SpecificPosition.SINGLE_BLOCK;
        } else {
            if (numberOfCurrentBlock == 0) {
                return SpecificPosition.FIRST_BLOCK;
            } else if (numberOfCurrentBlock == countOfAllBlocks - 1) {
                return SpecificPosition.LAST_BLOCK;
            } else {
                return SpecificPosition.INTERMEDIATE_BLOCK;
            }
        }
    }

    public List<String> getTokensList() {
        return tokensList;
    }

    public int getNumberOfCurrentBlock() {
        return numberOfCurrentBlock;
    }

    public int getCountOfAllBlocks() {
        return countOfAllBlocks;
    }

    /**
     * Relative position of block in the pipeline
     */
    public enum SpecificPosition {
        SINGLE_BLOCK,          // first and last block
        FIRST_BLOCK,
        LAST_BLOCK,
        INTERMEDIATE_BLOCK
    }
}
