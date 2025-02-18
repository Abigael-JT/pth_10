/*
 * Teragrep Data Processing Language (DPL) translator for Apache Spark (pth_10)
 * Copyright (C) 2019-2024 Suomen Kanuuna Oy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 * Additional permission under GNU Affero General Public License version 3
 * section 7
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with other code, such other code is not for that reason alone subject to any
 * of the requirements of the GNU Affero GPL version 3 as long as this Program
 * is the same Program as licensed from Suomen Kanuuna Oy without any additional
 * modifications.
 *
 * Supplemented terms under GNU Affero General Public License version 3
 * section 7
 *
 * Origin of the software must be attributed to Suomen Kanuuna Oy. Any modified
 * versions must be marked as "Modified version of" The Program.
 *
 * Names of the licensors and authors may not be used for publicity purposes.
 *
 * No rights are granted for use of trade names, trademarks, or service marks
 * which are in The Program if any.
 *
 * Licensee must indemnify licensors and authors for any liability that these
 * contractual assumptions impose on licensors and authors.
 *
 * To the extent this program is licensed as part of the Commercial versions of
 * Teragrep, the applicable Commercial License may apply to this file if you as
 * a licensee so wish it.
 */
package com.teragrep.pth10.translationTests;

import com.teragrep.pth10.ast.DPLParserCatalystContext;
import com.teragrep.pth10.ast.commands.transformstatement.ChartTransformation;
import com.teragrep.pth10.steps.chart.ChartStep;
import com.teragrep.pth_03.antlr.DPLLexer;
import com.teragrep.pth_03.antlr.DPLParser;
import com.teragrep.pth_03.shaded.org.antlr.v4.runtime.CharStream;
import com.teragrep.pth_03.shaded.org.antlr.v4.runtime.CharStreams;
import com.teragrep.pth_03.shaded.org.antlr.v4.runtime.CommonTokenStream;
import com.teragrep.pth_03.shaded.org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChartTest {

    @Test
    void testChartTranslation() {
        String query = "| chart count(_raw) by _time";
        CharStream inputStream = CharStreams.fromString(query);
        DPLLexer lexer = new DPLLexer(inputStream);
        DPLParser parser = new DPLParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.root();

        DPLParserCatalystContext ctx = new DPLParserCatalystContext(null);
        ctx.setEarliest("-1w");

        ChartTransformation ct = new ChartTransformation(ctx);
        ct.visitChartTransformation((DPLParser.ChartTransformationContext) tree.getChild(1).getChild(0));
        ChartStep cs = ct.chartStep;

        Assertions
                .assertEquals(
                        "[countaggregator(input[0, java.lang.Long, true].longValue AS value, staticinvoke(class java.lang.Long, ObjectType(class java.lang.Long), valueOf, input[0, bigint, true], true, false, true), input[0, java.lang.Long, true].longValue) AS `count(_raw)`]",
                        Arrays.toString(cs.getListOfAggrExpressions().toArray())
                );
        Assertions.assertEquals("[_time]", Arrays.toString(cs.getGroupByList().toArray()));

    }

    @Test
    void testChartTranslation_multiGroupBy() {
        String query = "| chart count(_raw) by _time, fieldTwo";
        CharStream inputStream = CharStreams.fromString(query);
        DPLLexer lexer = new DPLLexer(inputStream);
        DPLParser parser = new DPLParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.root();

        DPLParserCatalystContext ctx = new DPLParserCatalystContext(null);
        ctx.setEarliest("-1w");

        ChartTransformation ct = new ChartTransformation(ctx);
        ct.visitChartTransformation((DPLParser.ChartTransformationContext) tree.getChild(1).getChild(0));
        ChartStep cs = ct.chartStep;

        Assertions
                .assertEquals(
                        "[countaggregator(input[0, java.lang.Long, true].longValue AS value, staticinvoke(class java.lang.Long, ObjectType(class java.lang.Long), valueOf, input[0, bigint, true], true, false, true), input[0, java.lang.Long, true].longValue) AS `count(_raw)`]",
                        Arrays.toString(cs.getListOfAggrExpressions().toArray())
                );
        Assertions.assertEquals("[_time, fieldTwo]", Arrays.toString(cs.getGroupByList().toArray()));
    }

    @Test
    void testChartTranslation_multiGroupByNoComma() {
        String query = "| chart count(_raw) by _time fieldTwo";
        CharStream inputStream = CharStreams.fromString(query);
        DPLLexer lexer = new DPLLexer(inputStream);
        DPLParser parser = new DPLParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.root();

        DPLParserCatalystContext ctx = new DPLParserCatalystContext(null);
        ctx.setEarliest("-1w");

        ChartTransformation ct = new ChartTransformation(ctx);
        ct.visitChartTransformation((DPLParser.ChartTransformationContext) tree.getChild(1).getChild(0));
        ChartStep cs = ct.chartStep;

        Assertions
                .assertEquals(
                        "[countaggregator(input[0, java.lang.Long, true].longValue AS value, staticinvoke(class java.lang.Long, ObjectType(class java.lang.Long), valueOf, input[0, bigint, true], true, false, true), input[0, java.lang.Long, true].longValue) AS `count(_raw)`]",
                        Arrays.toString(cs.getListOfAggrExpressions().toArray())
                );
        Assertions.assertEquals("[fieldTwo, _time]", Arrays.toString(cs.getGroupByList().toArray()));

    }
}
