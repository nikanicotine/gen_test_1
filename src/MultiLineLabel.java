import java.awt.*;


public class MultiLineLabel extends Canvas {
    protected String text = new String();
    protected int alignment = LEFT;
    private String[] lines;
    private boolean placed = false;
    protected Dimension dimension = new Dimension();
    private int lineHeight, lineAscent, lineWidth[];

    public static final int LEFT = 0, RIGHT = 1, CENTER = 2;

    public MultiLineLabel() {
        this("");
    }

    public MultiLineLabel(String text) {
        this(text, LEFT);
    }

    public MultiLineLabel(String[] lines) {
        this(lines, LEFT);
    }

    public MultiLineLabel(String text, int alignment) {
        setText(text);
        setAlignment(alignment);
    }

    public MultiLineLabel(String[] lines, int alignment) {
        setTextLines(lines);
        setAlignment(alignment);
    }

    public String getText() {
        return text;
    }

    public String[] getTextLines() {
        return lines;
    }

    public int getAlignment() {
        return alignment;
    }

    public void setText(String text) {
        if (!this.text.equals(text)) {
            this.text = text;
            this.lines = Utils.stringTokenizer(text, "\n");
            placed = false;
        }
    }

    public void setTextLines(String[] lines) {
        if (this.lines != lines) {
            StringBuffer strbuf = new StringBuffer();
            for (int i = 0; i < lines.length; i++) strbuf.append(lines[i] + "\n");
            this.text = strbuf.deleteCharAt(strbuf.length() - 1).toString();
            this.lines = lines;
            placed = false;
        }
    }

    public void setAlignment(int alignment) {
        switch (alignment) {
            case LEFT:
            case RIGHT:
            case CENTER:
                this.alignment = alignment;
                return;
        }
        throw new IllegalArgumentException("improper alignment: " + alignment);
    }

    public void setFont(Font font) {
        super.setFont(font);
        placed = false;
    }

    void place() {
        FontMetrics fm = getFontMetrics(getFont());
        lineHeight = fm.getHeight();
        lineAscent = fm.getAscent();
        lineWidth = new int[lines.length];
        dimension.width = 0;
        for (int i = 0; i < lines.length; i++)
            if ((lineWidth[i] = fm.stringWidth(lines[i])) > dimension.width) dimension.width = lineWidth[i];
        dimension.height = lineHeight * lines.length;
        placed = true;
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public Dimension getPreferredSize() {
        if (!placed) place();
        return dimension;
    }

    public void paint(Graphics g) {
        if (!placed) place();
        int x = 0, y = lineAscent;
        for (int i = 0; i < lines.length; i++, y += lineHeight) {
            switch (alignment) {
                case LEFT:
                    x = 0;
                    break;
                case RIGHT:
                    x = dimension.width - lineWidth[i];
                    break;
                case CENTER:
                    x = (dimension.width - lineWidth[i]) / 2;
                    break;
            }
            g.drawString(lines[i], x, y);
        }
    }
}