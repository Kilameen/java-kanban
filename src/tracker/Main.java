package tracker;

import tracker.manager.Managers;
import tracker.manager.TaskManager;
import tracker.tasks.Epic;
import tracker.tasks.Status;
import tracker.tasks.SubTask;
import tracker.tasks.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefault();

        //Создаем 2 задачи

        Task task1 = new Task("Task_1", "Task_desc_1", Status.NEW);
        Task task2 = new Task("Task_2", "Task_desc_2", Status.IN_PROGRESS);

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        //Создаем 2 эпика, один эпик с 3 подзадачами, один пустой

        Epic epic1 = new Epic("Epic_1", "Epic_desc_1");
        Epic epic2 = new Epic("Epic_2", "Epic_desc_2");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());

        SubTask subtask1 = new SubTask("Subtask_1_1", "Subtask_desc_1_1", Status.NEW, epic1.getId());
        SubTask subtask2 = new SubTask("Subtask_1_2", "Subtask_desc_1_2", Status.IN_PROGRESS, epic1.getId());
        SubTask subtask3 = new SubTask("Subtask_2_1", "Subtask_desc_2_1", Status.DONE, epic1.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getSubtaskById(subtask3.getId());

        //Запрашиваем созданные задачи несколько раз в разном порядке.

        System.out.println("\n Запрос созданной задачи : \n" + "\n" + taskManager.getTaskById(1));
        System.out.println("Запрос созданной задачи : \n" + "\n" + taskManager.getTaskById(2));
        System.out.println("Запрос созданной задачи : \n" + "\n" + taskManager.getTaskById(1));

        System.out.println("\n Показать историю : \n" + taskManager.getHistory());

        System.out.println("\n Запрос созданной задачи : \n" + "\n" + taskManager.getTaskById(2));
        System.out.println("Запрос созданной задачи : \n" + "\n" + taskManager.getTaskById(1));
        System.out.println("Запрос созданной задачи : \n" + "\n" + taskManager.getTaskById(2));

        System.out.println("\n Показать историю : \n" + taskManager.getHistory());

        //Удаляем задчу и проверям историю
        System.out.println("Удаляем задачу: " + task1.getName());
        taskManager.deleteTask(task1.getId());
        System.out.println("\n Показать историю : \n" + taskManager.getHistory());

        //Удаляем эпик и проверяем историю
        System.out.println("Удаляем эпик: " + epic1.getName());
        taskManager.deleteEpic(epic1.getId());
        System.out.println("\n Показать историю : \n" + taskManager.getHistory());

        System.out.println("_".repeat(100));
        System.out.println("Выводы в консоль 5-го спринта");
        printAllTasks(taskManager);

        final Task task = taskManager.getTaskById(task2.getId());
        task.setStatus(Status.DONE);
        taskManager.updateTask(task2);

        printAllTasks(taskManager);

        System.out.println("\nУдалить все задачи:");
        taskManager.deleteAllTasks();
        printAllTasks(taskManager);
    }

    public static void printAllTasks(TaskManager taskManager) {
        System.out.println("\nTasks:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task.toString());
        }

        System.out.println("\nEpics:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println("\n" + epic.toString());
            for (SubTask subTask : taskManager.getSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + subTask.toString());
            }
        }

        System.out.println("\nSubtasks:");
        for (SubTask subTask : taskManager.getSubtasks()) {
            System.out.println(subTask.toString());
        }

        System.out.println("\nHistory:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}