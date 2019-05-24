package com.myself.everythingP.cmd;


import com.myself.everythingP.config.EverythingPConfig;
import com.myself.everythingP.core.EverythingPManager;
import com.myself.everythingP.core.common.Message;
import com.myself.everythingP.core.model.Condition;
import com.myself.everythingP.core.model.FileType;
import com.myself.everythingP.core.model.Thing;


import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class everythingCmdApp {

    private static Scanner scanner = new Scanner(System.in);
    private static LinkedList<String> historicalRecordsList = new LinkedList<>();
    private static int maxNum = 5;


    public static void main(String[] args) {
        param(args);
        welcome();
        EverythingPManager manager = EverythingPManager.getInstance();
//        manager.startThingClearThread();
        manager.startFileSystemMonitor();
        interactive(manager);
    }

    private static void printHR(LinkedList<String> historicalRecordsList) {
        if (historicalRecordsList.size()==0) {
            System.out.println("暂无历史搜索记录");
        } else {
            System.out.println("历史搜索记录：");
            for (String l : historicalRecordsList) {
                System.out.print(l + " ");
            }
            System.out.println();
        }
    }

    private static void param(String[] args) {
        EverythingPConfig config = EverythingPConfig.getInstance();
        for (String param : args) {
            String maxReturnStr = "--maxReturnThingRecode=";
            if (param.startsWith(maxReturnStr)) {
                int index = param.indexOf("=");
                if (param.length() > index + 1) {
                    String maxReturnParam = param.substring(index + 1);
                    config.setMaxReturnThingRecode(Integer.parseInt(maxReturnParam));
                }
            }

            String orderByAscStr = "--orderByAsc=";
            if (param.startsWith(orderByAscStr)) {
                int index = param.indexOf("=");
                if (param.length() > index + 1) {
                    String orderByAscParam = param.substring(index + 1);
                    config.setOrderByAsc(Boolean.parseBoolean(orderByAscParam));
                }
            }

            String includePathStr = "--includePath=";
            if (param.startsWith(includePathStr)) {
                int index = param.indexOf("=");
                if (param.length() > index + 1) {
                    //输入时以逗号隔开
                    String[] includePaths = param.substring(index + 1).split(";");
                    config.getIncludePath().clear();
                    for (String includePath : includePaths) {
                        config.getIncludePath().add(includePath);
                    }
                }
            }

            String excludePathStr = "--excludePath=";
            if (param.startsWith(excludePathStr)) {
                int index = param.indexOf("=");
                if (param.length() > index + 1) {
                    String[] excludePaths = param.substring(index + 1).split(";");
                    config.getExcludePath().clear();
                    for (String excludePath : excludePaths) {
                        config.getExcludePath().add(excludePath);
                    }
                }
            }
        }
    }

    private static void interactive(EverythingPManager manager) {
        while (true) {
            printHR(historicalRecordsList);
            System.out.print(">>");
            String line = scanner.nextLine();
            if (line.startsWith("search")) {
                String[] values = line.split(" ");
                if (!values[0].equals("search")) {
                    help();
                    continue;
                }
                if (values.length >= 2) {
                    Condition condition = new Condition();
                    condition.setName(values[1]);
                    StringBuilder sb = new StringBuilder(values[1]);
                    if (values.length >= 3) {
                        boolean flag = false;
                        for (FileType fileType : FileType.values()) {
                            if (fileType.name().equalsIgnoreCase(values[2])) {
                                flag = true;
                                condition.setFileType(values[2].toUpperCase());
                                sb.append("(").append(values[2].toUpperCase()).append(")");
                            }
                        }
                        if (!flag) {
                            help();
                            continue;
                        }
                    }
                    String record = sb.toString();
                    if (historicalRecordsList.contains(record)) {
                        historicalRecordsList.remove(record);
                    }
                    if (historicalRecordsList.size() == maxNum) {//没到达存储历史记录最大个数的情况下
                        historicalRecordsList.removeLast();
                    }
                    historicalRecordsList.addFirst(record);
                    search(manager, condition);
                    continue;
                } else {
                    help();
                    continue;
                }
            }
            switch (line) {
                case "help":
                    help();
                    break;
                case "quit":
                    quit();
                    break;
                case "index":
                    index(manager);
                    break;
                default:
                    help();
            }
        }
    }

    private static void search(EverythingPManager manager, Condition condition) {
        condition.setLimit(EverythingPConfig.getInstance().getMaxReturnThingRecode());
        condition.setOrderByAsc(EverythingPConfig.getInstance().getOrderByAsc());
        List<Thing> list = manager.search(condition);
        for (Thing thing : list) {
            System.out.println(Message.print(thing));
        }
    }

    private static void index(EverythingPManager manager) {
        new Thread(manager::buildIndex).start();
    }

    private static void quit() {
        System.out.println("谢谢使用，再见");
        System.exit(0);
    }

    private static void help() {
        System.out.println("命令行如下：");
        System.out.println("帮助：help");
        System.out.println("退出：quit");
        System.out.println("索引：index");
        System.out.println("检索：search <name> [<fileType> img | doc | bin | archive | other]");
    }

    private static void welcome() {
        System.out.println("欢迎使用Everything");
    }
}
