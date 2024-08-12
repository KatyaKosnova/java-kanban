# Трекер задач


## Описание задачи
Как системы контроля версий помогают команде работать с общим кодом, так и трекеры задач позволяют эффективно организовать совместную работу над задачами. Вам предстоит написать бэкенд для такого трекера. В итоге должна получиться программа, отвечающая за формирование модели данных для этой страницы

## Типы задач
Простейшим кирпичиком такой системы является задача (англ. task). У задачи есть следующие свойства:
- Название, кратко описывающее суть задачи (например, «Переезд»).
- Описание, в котором раскрываются детали.
- Уникальный идентификационный номер задачи, по которому её можно будет найти.
- Статус, отображающий её прогресс. Мы будем выделять следующие этапы жизни задачи:
  - NEW — задача только создана, но к её выполнению ещё не приступили.
  - IN_PROGRESS — над задачей ведётся работа.
  - DONE — задача выполнена.

Иногда для выполнения какой-нибудь масштабной задачи её лучше разбить на подзадачи (англ. subtask). Большую задачу, которая делится на подзадачи, мы будем называть эпиком (англ. epic).
Таким образом, в нашей системе задачи могут быть трёх типов: обычные задачи, эпики и подзадачи. Для них должны выполняться следующие условия:
- Для каждой подзадачи известно, в рамках какого эпика она выполняется.
- Каждый эпик знает, какие подзадачи в него входят.
- Завершение всех подзадач эпика считается завершением эпика.

## Менеджер
Кроме классов для описания задач, вам нужно реализовать класс для объекта-менеджера. Он будет запускаться на старте программы и управлять всеми задачами. В нём должны быть реализованы следующие функции:
1. Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
2. Методы для каждого из типа задач(Задача/Эпик/Подзадача):
   1. Получение списка всех задач.
   2. Удаление всех задач.
   3. Получение по идентификатору.
   4. Создание. Сам объект должен передаваться в качестве параметра.
   5. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
   6. Удаление по идентификатору.
3. Дополнительные методы:
   1. Получение списка всех подзадач определённого эпика.
4. Управление статусами осуществляется по следующему правилу:
   1. Менеджер сам не выбирает статус для задачи. Информация о нём приходит менеджеру вместе с информацией о самой задаче. По этим данным в одних случаях он будет сохранять статус, в других будет рассчитывать.
   2. Для эпиков:
      1. если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
      2. если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
      3. во всех остальных случаях статус должен быть IN_PROGRESS.