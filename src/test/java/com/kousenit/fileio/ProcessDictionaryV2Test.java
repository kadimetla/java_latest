package com.kousenit.fileio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcessDictionaryV2Test {
    private final ProcessDictionaryV2 pd = new ProcessDictionaryV2();

    @Test
    void testPrintNLongestWords() {
        // Verify method executes without exception
        assertDoesNotThrow(() -> pd.printNLongestWords(5));
    }

    @Test
    void testPrintNLongestWordsWithDifferentValues() {
        // Test with different n values
        assertDoesNotThrow(() -> pd.printNLongestWords(10));
        assertDoesNotThrow(() -> pd.printNLongestWords(3));
        assertDoesNotThrow(() -> pd.printNLongestWords(1));
    }

    @Test
    void testPrintWordsOfEachLength() {
        // Verify method executes without exception
        assertDoesNotThrow(pd::printWordsOfEachLength);
    }

    @Test
    void testPrintHowManyWordsOfEachLength() {
        // Verify method executes without exception
        assertDoesNotThrow(pd::printHowManyWordsOfEachLength);
    }

    @Test
    void testTeeingCollectorExample() {
        // This is the featured example in the slides (lines 171-194)!
        // Demonstrates teeing collector with nested collectors
        assertDoesNotThrow(pd::teeingCollectorExample);
    }

    @Test
    void testPrintSortedMapOfWords() {
        // Verify method executes without exception
        assertDoesNotThrow(pd::printSortedMapOfWords);
    }

    @Test
    void testMainMethodExecutes() {
        // Test that main() runs all the demonstrations
        assertDoesNotThrow(() -> ProcessDictionaryV2.main(new String[]{}));
    }

    @Test
    void testPrintNLongestWordsEdgeCases() {
        // Edge case: n = 0
        assertDoesNotThrow(() -> pd.printNLongestWords(0));

        // Test with large number
        assertDoesNotThrow(() -> pd.printNLongestWords(100));
    }

    @Test
    void testAllPublicMethodsExecute() {
        // Comprehensive test: run all public methods
        assertAll("All public methods execute successfully",
                () -> assertDoesNotThrow(() -> pd.printNLongestWords(5)),
                () -> assertDoesNotThrow(pd::printWordsOfEachLength),
                () -> assertDoesNotThrow(pd::printHowManyWordsOfEachLength),
                () -> assertDoesNotThrow(pd::teeingCollectorExample),
                () -> assertDoesNotThrow(pd::printSortedMapOfWords)
        );
    }
}
