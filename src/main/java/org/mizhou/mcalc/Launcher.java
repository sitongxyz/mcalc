package org.mizhou.mcalc;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.InfoCmp;
import org.mizhou.mcalc.exception.UnknownFunctionException;
import org.mizhou.mcalc.function.FunctionFactory;
import org.mizhou.mcalc.token.Num;

/**
 * 启动器
 *
 * @author Michael Chow <mizhoux@gmail.com>
 */
public class Launcher {

    public static void main(String[] args) throws Exception {
        System.out.println("欢迎使用你的计算器（输入 /e(xit) 退出）");

        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();

        LineReader reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(new StringsCompleter(FunctionFactory.getFunctionNames()))
                .build();

        String prompt = new AttributedStringBuilder()
                .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN))
                .append(">> ")
                .toAnsi();

        while (true) {
            String line;

            try {
                line = reader.readLine(prompt);
            } catch (UserInterruptException e) { // Ctrl-C
                continue;
            } catch (EndOfFileException e) { // Ctrl-D
                break;
            }

            line = removeWhitespace(line);

            if (line.isEmpty()) {
                continue;
            }

            if ("/e".equalsIgnoreCase(line) || "/exit".equalsIgnoreCase(line)) {
                break;
            }

            if ("/c".equalsIgnoreCase(line) || "/clear".equalsIgnoreCase(line)) {
                terminal.puts(InfoCmp.Capability.clear_screen);
                continue;
            }

            doOperate(line);
        }
    }

    private static void doOperate(String line) {
        try {
            Expression expr = Expression.parse(line);
            Expression postfixExpr = expr.toPostfixExpr();
            Num result = postfixExpr.calculate();

            System.out.println(result);

        } catch (UnknownFunctionException ex) {
            System.out.println(ex.getMessage());
        } catch (ArithmeticException ex) {
            System.out.println("运算错误：" + ex.getMessage());
        } catch (RuntimeException ex) {
            System.out.println("运行错误：" + ex.getMessage());
            // ex.printStackTrace(System.err);
        }
    }

    private static String removeWhitespace(String line) {
        StringBuilder result = new StringBuilder(line.length());

        for (int i = 0, l = line.length(); i < l; i++) {
            char ch = line.charAt(i);

            if (!Character.isWhitespace(ch)) {
                result.append(ch);
            }
        }

        return result.toString();
    }
}
