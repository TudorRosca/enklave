__author__ = 'radu'


def get_pagination_from_request(request):
    page = request.GET.get('page')
    if not page:
        return 0, 29

    page = secure_page(request.GET['page'])
    offset, limit = get_offset_and_limit_from_page(page)

    return offset, limit


def secure_page(page):
    try:
        page = int(page)
    except ValueError:
        return 1

    if page < 1:
        return 1

    return page


def format_oauth_data(access_token, refresh_token):
    data = {
        'access_token': access_token.token,
        'refresh_token': refresh_token.token,
        'expires': access_token.expires
    }

    return data


def get_offset_and_limit_from_page(page):
    return (page-1)*30, page*30 - 1


def format_user_stats(user, user_profile):

    data = {
        'id': user.id,
        'experience': user_profile.experience or 0,
        'scrap': user_profile.scrap or 0,
        'distance_walked': user_profile.distance_walked or 0,
        'merit': user_profile.merit or 0,
        'energy': user_profile.energy or 0,
    }

    return data


def format_user_public_data(user, user_profile):

    data = {
        'id': user.id,
        'username': user.username,
        'date_joined': user.date_joined,
        'experience': user_profile.experience or 0
    }

    return data




