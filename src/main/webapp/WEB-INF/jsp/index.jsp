<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Тестовое задание для Quick Resto</title>
    <link href="/css/bootstrap.min.css" rel="stylesheet">

    <style>
        body > div > header {
            margin-bottom: 26px;
        }

        .note-title > span {
            font-size: 32px;
            font-weight: bold;
            margin-bottom: 10px;
        }

        .note-title-controls {
            float: right;
        }

        .note-footer-dates {
            width: 100%;
            text-align: right;
            color: gray;
        }

        .note-footer-dates > span {
            font-style: italic;
        }
    </style>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jsrender/0.9.83/jsrender.min.js"></script>
    <script src="/js/bootstrap.min.js"></script>

    <script id="noteElementTemplate" type="text/x-jsrender">
            <div class="well" id="note-container-{{:id}}">
                <div class="note-title">
                    <span id="note-title-{{:id}}">{{:title}}</span>
                    <div class="note-title-controls">
                        <a href="#" data-id="{{:id}}" class="btn btn-xs btn-primary editBtn">Редактировать</a>
                        <a href="#" data-id="{{:id}}" class="btn btn-xs btn-danger removeBtn">Удалить</a>
                    </div>
                </div>
                <p id="note-text-{{:id}}">
                        {{:text}}
                </p>
                <div class="note-footer-dates">
                    Заметка создана: <span>{{:createdDate}}</span><br/>
                    Последние изменение: <span id="note-date-last-modified-{{:id}}">{{:lastModifiedDate}}</span>
                </div>
            </div>
    </script>

    <script>
        var lastFilter = '';

        function initControlButtons() {
            $(".editBtn").unbind('click');
            $(".removeBtn").unbind('click');

            $(".editBtn").each(function () {
                $(this).click(function () {
                    var id = $(this).attr("data-id");

                    $.getJSON("/notes/" + id, function (data) {
                        $("#modal-note-title").val(data.note.title);
                        $("#modal-note-text").val(data.note.text);

                        $("#modal-save-button").unbind('click');
                        $("#modal-save-button").click(function () {
                            $.ajax({
                                type: "PUT",
                                url: "/notes/" + id,
                                contentType: "application/json",
                                data: JSON.stringify({
                                    title: $("#modal-note-title").val(),
                                    text: $("#modal-note-text").val()
                                })
                            }).always(function (data) {
                                if (data.status !== "success") {
                                    alert("Ошибка во время редактирования!\n\n    " + data.responseJSON.error.code + " " + data.responseJSON.error.name);
                                } else {
                                    if (lastFilter !== '') {
                                        applyFilter();
                                    }

                                    $("#note-title-" + id).text(data.note.title);
                                    $("#note-text-" + id).text(data.note.text);
                                    $("#note-date-last-modified-" + id).text(data.note.lastModifiedDate);

                                    $("#modal-save-button").unbind('click');
                                    $("#editModal").modal('hide');
                                }
                            });
                        });

                        $("#modal-save-button").text('Сохранить');
                        $("#noteModalLabel").text('Редактирование заметки');
                        $('#editModal').modal();
                    });

                    return false;
                });
            });

            $(".removeBtn").each(function () {
                $(this).click(function () {
                    var id = $(this).attr("data-id");

                    var result = confirm('Вы действительно хотите удалить заметку "' + $("#note-title-" + id).text() + '"?');

                    if (result) {
                        $.ajax({
                            type: "DELETE",
                            url: "/notes/" + id
                        }).always(function (data) {
                            if (data.status !== "success") {
                                alert("Ошибка во время удаления!\n\n    " + data.responseJSON.error.code + " " + data.responseJSON.error.name);
                            } else {
                                $("#note-container-" + id).remove();
                            }
                        });
                    }

                    return false;
                });
            });
        }

        function applyFilter() {
            var filter = $("#notes-filter-input").val();
            $.getJSON("/notes/?filter=" + filter, function (data) {
                $("#note-holder").empty();

                $("#note-holder").html(
                        $.templates("#noteElementTemplate").render(data)
                );

                initControlButtons();

                lastFilter = filter;
            });
        }

        $(function () {
            initControlButtons();

            $("#add-new-note").click(function () {
                $("#modal-note-title").val('');
                $("#modal-note-text").val('');

                $("#modal-save-button").unbind('click');
                $("#modal-save-button").click(function () {
                    if ($("#modal-note-title").val() === '') {
                        alert('Введите заголовок заметки!');
                        return false;
                    }

                    $.ajax({
                        type: "POST",
                        url: "/notes",
                        contentType: "application/json",
                        data: JSON.stringify({
                            title: $("#modal-note-title").val(),
                            text: $("#modal-note-text").val()
                        })
                    }).always(function (data) {
                        if (data.status !== "success") {
                            alert("Ошибка во время создания!\n\n    " + data.responseJSON.error.code + " " + data.responseJSON.error.name);
                        } else {
                            if (lastFilter !== '') {
                                applyFilter();
                            } else {
                                $("#note-holder").append(
                                        $.templates("#noteElementTemplate").render(data.note)
                                );
                            }

                            initControlButtons();

                            $("#modal-save-button").unbind('click');
                            $("#editModal").modal('hide');
                        }
                    });
                });

                $("#modal-save-button").text('Добавить');
                $("#noteModalLabel").text('Добавление новой заметки');
                $('#editModal').modal();
            });

            $("#notes-filter-input").keyup(function () {
                var filter = $("#notes-filter-input").val();
                if (lastFilter !== filter) {
                    applyFilter();
                }
            });

            $("#show-top-5-btn").click(function () {
                var startDate = prompt("Введите начало временн́ого интервала создания заметки\n\nФормат: dd.MM.yyyy или dd.MM.yyyy HH:mm:ss\n\nЕсли значение не введено, то поиск будет осуществляться без ограничения по началу", '');
                var endDate = prompt("Введите конец временн́ого интервала создание заметки\n\nФормат: dd.MM.yyyy или dd.MM.yyyy HH:mm:ss\n\nЕсли значение не введено, то поиск будет осуществляться без ограничения по концу", '');

                $.ajax({
                    type: "GET",
                    url: "/notes/top5?start=" + encodeURIComponent(startDate) + "&end=" + encodeURIComponent(endDate)
                }).always(function (data) {
                    if (data.status !== "success") {
                        alert("Ошибка во время получения списка!\n\n    " + data.responseJSON.error.code + " " + data.responseJSON.error.name);
                    } else {
                        var top5 = "Топ-5 слов за период " + data.top.startDate + " - " + data.top.endDate + ":\n";

                        var i = 1;
                        data.top.list.forEach(function (el) {
                            top5 += "\n     " + i++ + ". " + el;
                        });

                        alert(top5);
                    }
                });

                return false;
            });
        });
    </script>
</head>
<body>
<div class="container">
    <header>
        <h1>Заметки</h1>
        <hr/>
        <div class="row">
            <div class="col-md-6">
                <a href="#" class="btn btn-primary" id="add-new-note">Добавить заметку</a>
                <a href="#" class="btn btn-primary" id="show-top-5-btn">Показать топ-5 слов</a>
            </div>
            <div class="col-md-6" style="text-align: right">
                <form class="form-inline">
                    <div class="form-group">
                        <label for="notes-filter-input">Фильтр: </label>
                        <input type="text" class="form-control" id="notes-filter-input">
                    </div>
                </form>
            </div>
        </div>
        <hr/>
    </header>

    <div id="note-holder">
        <c:forEach var="note" items="${notes}">
            <div class="well" id="note-container-${note.id}">
                <div class="note-title">
                    <span id="note-title-${note.id}">${note.title}</span>
                    <div class="note-title-controls">
                        <a href="#" data-id="${note.id}" class="btn btn-xs btn-primary editBtn">Редактировать</a>
                        <a href="#" data-id="${note.id}" class="btn btn-xs btn-danger removeBtn">Удалить</a>
                    </div>
                </div>
                <p id="note-text-${note.id}">
                        ${note.text}
                </p>
                <div class="note-footer-dates">
                    Заметка создана: <span>${note.createdDate}</span><br/>
                    Последние изменение: <span id="note-date-last-modified-${note.id}">${note.lastModifiedDate}</span>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<div class="modal fade" id="editModal" tabindex="-1" role="dialog" aria-labelledby="noteModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="noteModalLabel">Редактирование заметки</h4>
            </div>
            <div class="modal-body">
                <form>
                    <div class="form-group">
                        <label for="modal-note-title" class="control-label">Заголовок:</label>
                        <input type="text" class="form-control" id="modal-note-title">
                    </div>
                    <div class="form-group">
                        <label for="modal-note-text" class="control-label">Текст:</label>
                        <textarea class="form-control" rows="10" id="modal-note-text"></textarea>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
                <button type="button" class="btn btn-primary" id="modal-save-button">Сохранить</button>
            </div>
        </div>
    </div>
</div>
</body>
</html>