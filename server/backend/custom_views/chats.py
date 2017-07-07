__author__ = 'radu'

from django.shortcuts import render, get_object_or_404

from backend.models import ChatRoom


def index(request):
    token = request.GET.get('token')
    context = {
        "token": token
    }
    return render(request, 'ws/socket.html', context)


# def chat_room(request, chat_room_id):
#     chat = get_object_or_404(ChatRoom, pk=chat_room_id)
#     return render(request, 'we/chat_room.html', {'chat': chat})
