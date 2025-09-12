import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.undo.UndoManager; // 对菜单活动事件撤销的实现

public class NotePad extends JFrame /// extends继承JFrame类
{

    int start = 0, end = 0;
    private UndoManager manager = new UndoManager();// 添加布局管理器
    private JTextArea text = new JTextArea();
    private JFileChooser jfc = new JFileChooser();
    private String title = "新建记事本";
    private File file;
    private JMenuBar menu;

    // ---------------菜单栏
    private JMenu File_bar, Edit_bar, Format_bar, View_bar, Help_bar;

    // ---------------文件菜单
    private JMenuItem File_bar_creat, File_bar_open, File_bar_save, File_bar_othersave, File_bar_exit;

    // ---------------编辑菜单
    private JMenuItem Edit_bar_Revoke, Edit_bar_shear, Edit_bar_copy, Edit_bar_paste, Edit_bar_delete, Format_bar_find_replace;

    // ---------------格式菜单
    private JMenuItem  Format_bar_ztxz, Format_bar_ztsz, Format_bar_ztdx;

    //----------------自动换行选项
    private JCheckBoxMenuItem Format_bar_hl;

    // ---------------关于、帮助菜单
    private JMenuItem  View_bar_about, Help_bar_help;

    private JLabel statusLabel1;
    private JToolBar statusBar;

    GregorianCalendar time = new GregorianCalendar();
    int year = time.get(Calendar.YEAR);
    int month = time.get(Calendar.MONTH);
    int day = time.get(Calendar.DAY_OF_MONTH);
    int hour = time.get(Calendar.HOUR_OF_DAY);
    int min = time.get(Calendar.MINUTE);
    int second = time.get(Calendar.SECOND);

    /* 文件格式过滤器 */
    public class filter extends javax.swing.filechooser.FileFilter {
        public boolean accept(File file) {
            String name = file.getName();
            name.toString(); // 该字符串中的数字被转换为字符
            /* 文件后缀是.txt且是个目录 */
            if (name.endsWith(".txt") || file.isDirectory()) {
                return true;
            } else
                return false;
        }

        /* 将引用具体子类的子类对象的方法,不可以省略类中的getDescription(),原因是编译器只允许调用在类中声明的方法. */
        public String getDescription() {
            return ".txt";
        }
    }

    /* 将菜单项 JMenu添加菜单 JMenuBar */
    public JMenu AddBar(String name, JMenuBar menu) {
        JMenu jmenu = new JMenu(name);
        menu.add(jmenu);
        return jmenu;
    }

    /* 将菜单项JMenuItem添加到菜单JMenu */
    public JMenuItem AddItem(String name, JMenu menu) {
        JMenuItem jmenu = new JMenuItem(name);
        menu.add(jmenu);
        return jmenu;
    }

    /*在右下方新建一个程序窗口*/
    public void newNotePad(){
        new NotePad().setLocation(getLocation().x+50,getLocation().y+50);
    }

    class Clock extends Thread { // 模拟时钟
        public void run() {
            while (true) {
                GregorianCalendar time = new GregorianCalendar();
                year = time.get(Calendar.YEAR);
                month = time.get(Calendar.MONTH);
                day = time.get(Calendar.DAY_OF_MONTH);
                hour = time.get(Calendar.HOUR_OF_DAY);
                min = time.get(Calendar.MINUTE);
                second = time.get(Calendar.SECOND);
                statusLabel1.setText(" 当前时间：" + hour + ":" + min + ":" + second + "   " + year + "/" + (month+1) + "/" + day);
                try {
                    Thread.sleep(950);
                } catch (InterruptedException exception) {
                }

            }
        }
    }

    NotePad note;
    //public void NotePad()
    {
        Container container = getContentPane();
        this.setTitle(title); // 设置窗口标题

        this.setSize(1500,1000);//设置窗口大小
        Dimension screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize();
        // 计算窗口位置，使窗口居中
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        this.setLocation(x, y);

        this.menu = new JMenuBar(); // 添加菜单 JMenuBar
        this.setJMenuBar(menu);// 调用this方法
        text.getDocument().addUndoableEditListener(manager);// 用于获得程序当前有效的文本
        /*
         * Font是JAVA中的字体类，PLAIN是Font类中的静态常量( static final ) ,表示是:普通样式常量 BOLD
         * :粗体样式常量 ,ITALIC: 斜体样式常量,28:磅
         */
        text.setFont(new Font("宋体", Font.PLAIN, 28));

        /* 光标颜色 */
        text.setCaretColor(Color.gray);

        /* 选中字体颜色 */
        text.setSelectedTextColor(Color.blue);

        /* 选中背景颜色 */
        text.setSelectionColor(Color.green);

        /* 是否换行 */
        text.setLineWrap(true);

        /* 是否单词边界换行（即有空白） */
        text.setWrapStyleWord(true);

        /* 文本区与边框的间距，四个参数分别为上、左、下、右 */
        text.setMargin(new Insets(3, 5, 3, 5));

        /* 创建一个 JScrollPane，它将视图组件显示在一个视口中，视图位置可使用一对滚动条控制 */
        add(new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

        File_bar = this.AddBar("文件(F)", menu);
        File_bar.setFont(new Font("黑体", Font.PLAIN, 24));
        Edit_bar = this.AddBar("编辑(E)", menu);
        Edit_bar.setFont(new Font("黑体", Font.PLAIN, 24));
        Format_bar = this.AddBar("格式(O)", menu);
        Format_bar.setFont(new Font("黑体", Font.PLAIN, 24));
        View_bar = this.AddBar("查看(V)", menu);
        View_bar.setFont(new Font("黑体", Font.PLAIN, 24));
        Help_bar = this.AddBar("帮助(H)", menu);
        Help_bar.setFont(new Font("黑体", Font.PLAIN, 24));

        /* 文件选项 */
        /* 新建选项 */
        File_bar_creat = this.AddItem("新建(N)   Ctrl+N", File_bar);
        File_bar_creat.setFont(new Font("黑体", Font.PLAIN, 20));
        File_bar_creat.addActionListener(new ActionListener() {
            // @Override
            public void actionPerformed(ActionEvent arg0) {
                // 新建一个新窗口
                newNotePad();
            }
        });

        /* 打开选项 */
        File_bar_open = this.AddItem("打开(O)   Ctrl+O", File_bar);
        File_bar_open.setFont(new Font("黑体", Font.PLAIN, 20));
        File_bar_open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    jfc.setCurrentDirectory(new File("."));// 设置当前目录
                    jfc.setFileFilter(new filter()); // 过滤文件

                    /*
                     * 确定是否将AcceptAll FileFilter用作可选择过滤器列表中一个可用选项。如果为假，
                     * 则AcceptAll文件过滤器从可用的文件过滤列表中删除。
                     * 如果为true，则AcceptAll文件过滤器将成为可用的文件过滤器。
                     */
                    jfc.setAcceptAllFileFilterUsed(false); // 全选文件

                    jfc.showOpenDialog(null); // 弹出一个 "Open File" 文件选择器对话框。
                    file = jfc.getSelectedFile(); // 获取已经选择目录
                    title = file.getName(); // 获取目录名
                    setTitle(title); // 显示目录名
                    int length = (int) (jfc.getSelectedFile()).length();
                    char[] ch = new char[length];
                    FileReader fr = new FileReader(file);
                    fr.read(ch);
                    title = new String(ch);
                    text.setText(title.trim()); // 获得对象的字段的值，然后转成string类型，并且去掉前后空白~~ToString()是转化为字符串的方法
                    // Trim()是去两边空格
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        });

        /* 保存选项 = (1)如果文件为空，新建一个目录保存；(2)如果当前文件存在，直接保存 */
        File_bar_save = this.AddItem("保存(S)   Ctrl+O", File_bar);
        File_bar_save.setFont(new Font("黑体", Font.PLAIN, 20));
        File_bar_save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (file == null) {
                    try {
                        jfc = new JFileChooser();
                        jfc.setCurrentDirectory(null);
                        title = JOptionPane.showInputDialog("请输入文件名：") + ".txt";
                        /*
                         * setSelectedFile返回的是对话框中选中的文件但如果对话框类型是showSaveDialog的话,
                         * 那么这里返回的值是你要保存的文件, 这个文件可能存在,可能不存在,就是你在对话框中输入的文件名了,
                         * 既然知道了文件,如果不存在,就新建一个,然后向文件写入数据,这样就可以实现保存了
                         */
                        jfc.setSelectedFile(new File(title));
                        jfc.setFileFilter(new filter());
                        int temp = jfc.showSaveDialog(null); // 获取当前对象
                        if (temp == jfc.APPROVE_OPTION) // 获得选中的文件对象
                        {
                            if (file != null)
                                file.delete();
                            file = new File(jfc.getCurrentDirectory(), title);
                            file.createNewFile();
                            FileWriter fw = new FileWriter(file);
                            fw.write(text.getText());
                            fw.close();
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e);
                    }
                } else {
                    try {
                        FileWriter fw = new FileWriter(file);
                        fw.write(text.getText());
                        fw.close();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e);
                    }
                }
            }
        });

        /* 另存为选项 */
        File_bar_othersave = this.AddItem("另存为(A)...", File_bar);
        File_bar_othersave.setFont(new Font("黑体", Font.PLAIN, 20));
        File_bar_othersave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                // file fw = new file();
                jfc = new JFileChooser();
                jfc.setCurrentDirectory(new File("."));
                try {
                    if (file == null) {
                        title = JOptionPane.showInputDialog("请输入文件名：") + ".txt";
                    } else
                        title = file.getName();//得到文件名字
                    jfc.setSelectedFile(new File(title));
                    jfc.setFileFilter(new filter());
                    int temp = jfc.showSaveDialog(null);
                    if (temp == jfc.APPROVE_OPTION) // 获得选中的文件对象
                    {
                        if (file != null)
                            file.delete();
                        file = new File(jfc.getCurrentDirectory(), title);
                        file.createNewFile();
                        FileWriter fw = new FileWriter(file);
                        fw.write(text.getText());
                        fw.close();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        });

        /* 将默认大小的分隔符添加到工具栏的末尾。 */
        File_bar.addSeparator();

        /* 退出选项 + 退出提示 */
        File_bar_exit = this.AddItem("退出(X)", File_bar);
        File_bar_exit.setFont(new Font("黑体", Font.PLAIN, 20));
        File_bar_exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int state = JOptionPane.showConfirmDialog(note, "您确定要退出？退出前请确定您的文件已保存");
                if (state == JOptionPane.OK_OPTION)
                    System.exit(0);
            }
        });

        /* 编辑选项 */
        /* 撤消选项 */
        Edit_bar_Revoke = this.AddItem("撤销(U)   Ctrl+Z", Edit_bar);
        Edit_bar_Revoke.setFont(new Font("黑体", Font.PLAIN, 20));
        Edit_bar_Revoke.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (manager.canUndo())
                    manager.undo();
            }
        });

        /* 剪切选项 */
        Edit_bar_shear = this.AddItem("剪切(T)   Ctrl+X", Edit_bar);
        Edit_bar_shear.setFont(new Font("黑体", Font.PLAIN, 20));
        Edit_bar_shear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                text.cut();
            }
        });

        /* 复制选项 */
        Edit_bar_copy = this.AddItem("复制(C)   Ctrl+C", Edit_bar);
        Edit_bar_copy.setFont(new Font("黑体", Font.PLAIN, 20));
        Edit_bar_copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                text.copy();
            }
        });

        /* 粘贴选项 */
        Edit_bar_paste = this.AddItem("粘贴(P)   Ctrl+V", Edit_bar);
        Edit_bar_paste.setFont(new Font("黑体", Font.PLAIN, 20));
        Edit_bar_paste.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                text.paste();
            }
        });

        /* 删除选项=用空格替换从当前选取的开始到结束 */
        Edit_bar_delete = this.AddItem("删除(L)   Del", Edit_bar);
        Edit_bar_delete.setFont(new Font("黑体", Font.PLAIN, 20));
        Edit_bar_delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                text.replaceRange("", text.getSelectionStart(), text.getSelectionEnd());
            }
        });

        Format_bar_find_replace = this.AddItem("替换(R)||查找(F)", Edit_bar);
        Format_bar_find_replace.setFont(new Font("黑体", Font.PLAIN, 20));
        Format_bar_find_replace.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JDialog search = new JDialog();
                search.setSize(500, 150);
                search.setLocation(1500, 500);
                JLabel label_1 = new JLabel("查找的内容");
                JLabel label_2 = new JLabel("替换的内容");
                JTextField textField_1 = new JTextField(5);
                JTextField textField_2 = new JTextField(5);
                JButton buttonFind = new JButton("查找下一个");
                JButton buttonChange = new JButton("替换");
                JPanel panel = new JPanel(new GridLayout(2, 3));
                panel.add(label_1);
                panel.add(textField_1);
                panel.add(buttonFind);
                panel.add(label_2);
                panel.add(textField_2);
                panel.add(buttonChange);
                search.add(panel);
                search.setVisible(true);
                // 为查找下一个 按钮绑定监听事件
                buttonFind.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String findText = textField_1.getText();// 查找的字符
                        String textArea = text.getText();// 当前文本框的内容

                        start = textArea.indexOf(findText, end);
                        end = start + findText.length();
                        if (start == -1) // 没有找到
                        {
                            JOptionPane.showMessageDialog(null, "没找到" + findText, "记事本", JOptionPane.WARNING_MESSAGE);
                            text.select(start, end);
                        } else {
                            text.select(start, end);
                        }

                    }
                });
                // 为替换按钮绑定监听时间

                buttonChange.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String changeText = textField_2.getText();// 替换的字符串
                        /* 如果选定文件为真 */
                        if (text.getSelectionStart() != text.getSelectionEnd())
                            text.replaceRange(changeText, text.getSelectionStart(), text.getSelectionEnd());
                    }
                });
            }
        });

        /* 自动换行选项 */
        Format_bar_hl = new JCheckBoxMenuItem("自动换行", true);
        Format_bar_hl.setFont(new Font("黑体", Font.PLAIN, 20));
        Format_bar.add(Format_bar_hl);
        Format_bar_hl.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                /* 根据文件名获取文件信息 */
                if (Format_bar_hl.getState())
                    text.setLineWrap(true);
                else
                    text.setLineWrap(false);
            }
        });

        /* 字体选项 */
        /*
         * 字体格式设置选项 GraphicsEnvironment 类描述了 Java(tm) 应用程序在特定平台上可用
         *
         * 的 GraphicsDevice 对象和 Font 对象的集合
         */
        Format_bar_ztxz = this.AddItem("字体选择(F)", Format_bar);
        Format_bar_ztxz.setFont(new Font("黑体", Font.PLAIN, 20));
        Format_bar_ztxz.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                /* 获取本地图形环境 */
                GraphicsEnvironment gr = GraphicsEnvironment.getLocalGraphicsEnvironment();
                /* 字体名称列表框 */
                JList fontName = new JList(gr.getAvailableFontFamilyNames());
                /* JScrollPane 管理视口、可选的垂直和水平滚动条以及可选的行和列标题视口 */
                int selection = JOptionPane.showConfirmDialog(null, new JScrollPane(fontName), "请选择字体",
                        JOptionPane.OK_CANCEL_OPTION);
                Object selectedFont = fontName.getSelectedValue();
                if (selection == JOptionPane.OK_OPTION && selectedFont != null) {
                    text.setFont(new Font(fontName.getSelectedValue().toString(), Font.PLAIN, text.getFont().getSize()));
                }
            }
        });

        /* 字号设置选项 */
        Format_bar_ztdx = this.AddItem("字号(S)", Format_bar);
        Format_bar_ztdx.setFont(new Font("黑体", Font.PLAIN, 20));
        Format_bar_ztdx.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Vector<Integer> fontSizeList = new Vector<>();
                for(int i=0;i<=35;i++){
                    fontSizeList.add(10+2*i);
                }
                /* 字体大小列表框 */
                JList fontSize = new JList(fontSizeList);
                /* JScrollPane 管理视口、可选的垂直和水平滚动条以及可选的行和列标题视口 */
                int selection = JOptionPane.showConfirmDialog(null, new JScrollPane(fontSize), "请选择字号",
                        JOptionPane.OK_CANCEL_OPTION);
                Object selectedSize = fontSize.getSelectedValue();
                if (selection == JOptionPane.OK_OPTION && selectedSize != null) {
                    text.setFont(new Font(text.getName(),Font.PLAIN,(int)selectedSize));
                }
            }
        });

        /* 字体颜色设置选项 */
        Format_bar_ztsz = this.AddItem("颜色(C)", Format_bar);
        Format_bar_ztsz.setFont(new Font("黑体", Font.PLAIN, 20));
        Format_bar_ztsz.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Color color = JColorChooser.showDialog(null, "文字颜色选择", Color.BLACK);
                text.setForeground(color);
            }
        });

        View_bar_about = this.AddItem("关于记事本(About)", View_bar);
        View_bar_about.setFont(new Font("黑体", Font.PLAIN, 20));
        View_bar_about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(null, "记事本\n开发语言：JAVA\n开发者：【xujiacheng】\n联系方式：2966394712@qq.com", "关于",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });

        Help_bar_help = this.AddItem("帮助选项(H)", Help_bar);
        Help_bar_help.setFont(new Font("黑体", Font.PLAIN, 20));
        Help_bar_help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(null, "拨打电话：16623068049", "帮助", JOptionPane.PLAIN_MESSAGE);
            }
        });

        // -----------------------------------创建和添加状态栏
        statusBar = new JToolBar();
        statusBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusLabel1 = new JLabel();
        statusLabel1.setFont(new Font("黑体", Font.PLAIN, 20));
        statusBar.add(statusLabel1);
        statusBar.addSeparator();
        container.add(statusBar, BorderLayout.SOUTH);
        statusBar.setVisible(true);
        Clock clock = new Clock();
        clock.start();

        this.setResizable(true); // 窗体是否可变
        this.setVisible(true); // 窗体是否可见
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    public static void main(String args[]) {
        NotePad example = new NotePad();
    }
}