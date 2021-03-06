"""
Django settings for enklave project.

Generated by 'django-admin startproject' using Django 1.8.6.

For more information on this file, see
https://docs.djangoproject.com/en/1.8/topics/settings/

For the full list of settings and their values, see
https://docs.djangoproject.com/en/1.8/ref/settings/
"""

# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
import os

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/1.8/howto/deployment/checklist/

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = 'bt&x%vq)d_qe(+$gn50-)h+8(yw*097=bwis!l20c&rkmrz^x4'

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = True

ALLOWED_HOSTS = []

import socket
RUNNING_DEV_SERVER = (socket.gethostname() == 'FRL-RST-DSK')

# Application definition

INSTALLED_APPS = (
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',

    'corsheaders',
    'kronos',
    'storages',
    'oauth2_provider',
    'social.apps.django_app.default',
    'rest_framework_social_oauth2',
    'rest_framework',
    'rest_framework_swagger',
    'backend',
    'silk',
    'django_jenkins'
)

MIDDLEWARE_CLASSES = (
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.auth.middleware.SessionAuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
    'django.middleware.security.SecurityMiddleware',
    'silk.middleware.SilkyMiddleware',
)

REST_FRAMEWORK = {
    'DEFAULT_AUTHENTICATION_CLASSES': (
        'oauth2_provider.ext.rest_framework.OAuth2Authentication',
        'rest_framework_social_oauth2.authentication.SocialAuthentication',
    ),
    'DEFAULT_PERMISSION_CLASSES': (
        'rest_framework.permissions.IsAuthenticated',
    ),
    'DEFAULT_PARSER_CLASSES': (
        'rest_framework.parsers.JSONParser',
    )
}

OAUTH2_PROVIDER = {
    # this is the list of available scopes
    'SCOPES': {'read': 'Read scope', 'write': 'Write scope'},
    # 'OAUTH2_VALIDATOR_CLASS': 'external.validators.EmailOAuth2Validator',
    'OAUTH2_BACKEND_CLASS': 'oauth2_provider.oauth2_backends.JSONOAuthLibCore'
}

ROOT_URLCONF = 'enklave.urls'

TEMPLATES = [
    {
        'BACKEND': 'django.template.backends.django.DjangoTemplates',
        # 'DIRS': [],
        'DIRS': [os.path.join(BASE_DIR, 'templates')],
        'APP_DIRS': True,
        'OPTIONS': {
            'context_processors': [
                'django.template.context_processors.debug',
                'django.template.context_processors.request',
                'django.contrib.auth.context_processors.auth',
                'django.contrib.messages.context_processors.messages',
                'social.apps.django_app.context_processors.backends',
                'social.apps.django_app.context_processors.login_redirect',
                ],
            },
        },
    ]

AUTHENTICATION_BACKENDS = (
    'social.backends.facebook.FacebookAppOAuth2',
    'social.backends.facebook.FacebookOAuth2',
    'social.backends.foursquare.FoursquareOAuth2',
    'social.backends.google.GoogleOAuth',
    'social.backends.google.GoogleOAuth2',
    'social.backends.google.GoogleOpenId',
    'social.backends.google.GooglePlusAuth',
    'social.backends.google.GoogleOpenIdConnect',
    'social.backends.vend.VendOAuth2',
    'social.backends.email.EmailAuth',
    'social.backends.username.UsernameAuth',
    'django.contrib.auth.backends.ModelBackend',
)

WSGI_APPLICATION = 'enklave.wsgi.application'


if RUNNING_DEV_SERVER:
    DATABASES = {
        'default': {
            'ENGINE': 'django.db.backends.postgresql_psycopg2',
            'NAME': 'enklave',
            'USER': 'test',
            'PASSWORD': 'test',
            'HOST': '192.168.0.1',
            'PORT': '',
        }
        # ,
        # 'default_readonly': {
        #     'ENGINE': 'django.db.backends.mysql',
        #     'USER': 'root',
        #     'NAME': 'enklave',
        #     'HOST': 'localhost',
        #     'PASSWORD': 'stoica',
        #     'READONLY': True,
        # }
    }
else:
    DATABASES = {
        'default': {
            'ENGINE': 'django.db.backends.postgresql_psycopg2',
            'NAME': 'enklave_dev',
            'USER': '',
            'PASSWORD': '',
            'HOST': '',
            'PORT': '',
        }
    }


# Internationalization
# https://docs.djangoproject.com/en/1.8/topics/i18n/

LANGUAGE_CODE = 'en-us'

TIME_ZONE = 'UTC'

USE_I18N = True

USE_L10N = True

USE_TZ = True


# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/1.8/howto/static-files/

SOCIAL_AUTH_FACEBOOK_KEY = ''
SOCIAL_AUTH_FACEBOOK_SECRET = ''

SOCIAL_AUTH_LOGIN_REDIRECT_URL = '/social/auth/'
SOCIAL_AUTH_LOGIN_URL = '/'
SOCIAL_AUTH_FACEBOOK_EXTENDED_PERMISSIONS = ['email', 'user_birthday', 'user_friends']
SOCIAL_AUTH_FACEBOOK_PROFILE_EXTRA_PARAMS = {
    'fields': 'id,name,email',
    }
SOCIAL_AUTH_FACEBOOK_SCOPE = ['email', 'user_birthday']
FACEBOOK_EXTENDED_PERMISSIONS = ['email', 'user_friends']


SOCIAL_AUTH_STRATEGY = 'social.strategies.django_strategy.DjangoStrategy'
SOCIAL_AUTH_STORAGE = 'social.apps.django_app.default.models.DjangoStorage'

SOCIAL_AUTH_GOOGLE_OAUTH2_KEY = ''
SOCIAL_AUTH_GOOGLE_OAUTH2_SECRET = ''


SOCIAL_AUTH_PIPELINE = (
    'social.pipeline.social_auth.social_details',
    'social.pipeline.social_auth.social_uid',
    'social.pipeline.social_auth.auth_allowed',
    'social.pipeline.social_auth.social_user',
    'social.pipeline.user.get_username',
    'social.pipeline.mail.mail_validation',
    'social.pipeline.user.create_user',
    # 'backend.pipeline.get_user_avatar',
    # 'backend.pipeline.update_user_friends',
    # 'backend.pipeline.associate_by_password',
    # 'social.pipeline.social_auth.associate_by_email',
    'social.pipeline.social_auth.associate_user',
    'social.pipeline.debug.debug',
    'social.pipeline.social_auth.load_extra_data',
    'social.pipeline.user.user_details',
    'social.pipeline.debug.debug',
    # 'backend.pipeline.update_user_social_data',
)


if RUNNING_DEV_SERVER:
    LOG_PATH = '/srv/'
else:
    LOG_PATH = '/srv/'

LOGGING = {
    'version': 1,
    'disable_existing_loggers': False,
    'filters': {
        'require_debug_false': {
            '()': 'django.utils.log.RequireDebugFalse'
        }
    },
    'formatters': {
        'verbose': {
            'format': '[contactor] %(levelname)s %(asctime)s %(message)s'
        },
        },
    'handlers': {
        'console': {
            # 'level': 'INFO',
            'level': 'DEBUG',
            'class': 'logging.StreamHandler',
            },
        'null': {
            'class': 'logging.NullHandler',
            },
        'mail_admins': {
            'level': 'ERROR',
            # 'filters': ['require_debug_false'],
            'class': 'django.utils.log.AdminEmailHandler',
            'email_backend': 'django_ses.SESBackend'
        },
        'daemon_status': {
            'level': 'INFO',
            'class': 'logging.handlers.RotatingFileHandler',
            'filename': '%s/django_daemon_status.log' % LOG_PATH,
            'maxBytes': 1024*1024*500  # 500 MB
        },
        'api_exceptions': {
            'level': 'DEBUG',
            'class': 'logging.handlers.RotatingFileHandler',
            'filename': '%s/django_exception_status.log' % LOG_PATH,
            'maxBytes': 1024*1024*500  # 500 MB
        },
        },
    'loggers': {
        'django': {
            'handlers': ['console'],
            },
        'django.request': {
            'handlers': ['mail_admins'],
            'level': 'ERROR',
            'propagate': False,
            },
        'django.security': {
            'handlers': ['mail_admins'],
            'level': 'ERROR',
            'propagate': False,
            },
        'py.warnings': {
            'handlers': ['console'],
            },
        'daemon_status': {
            'handlers': ['daemon_status'],
            'level': 'INFO',
            'propagate': True,
            },
        'api_exceptions': {
            'handlers': ['api_exceptions'],
            # 'handlers': ['mail_admins'],
            'level': 'DEBUG',
            'propagate': True,
            },
        # 'django.db': {
        #     'handlers': ['console'],
        #     'level': 'DEBUG',
        #     'propagate': False,
        # }
    },
    }

EMAIL_FROM_ADDR = ''
DEFAULT_FROM_EMAIL = ''
SERVER_EMAIL = ''
ADMIN_EMAIL = ''
SEND_BROKEN_LINK_EMAILS = True
ADMINS = (
    ('', ''),
)

MANAGERS = ADMINS
EMAIL_BACKEND = 'django_ses.SESBackend'
EMAIL_HOST = ''
EMAIL_PORT = 465

AWS_SES_REGION_NAME = 'us-west-2'
AWS_SES_REGION_ENDPOINT = 'email.us-west-2.amazonaws.com'

AWS_STORAGE_BUCKET_NAME = 'enklave-static'
AWS_LOCATION = '/static/'
STATICFILES_STORAGE = 'storages.backends.s3boto.S3BotoStorage'
STATIC_URL = 'http://' + AWS_STORAGE_BUCKET_NAME + '.s3.amazonaws.com/'
ADMIN_MEDIA_PREFIX = STATIC_URL + 'admin/'


# SWAGGER_SETTINGS = {
#     "exclude_namespaces": [], # List URL namespaces to ignore
#     "api_version": '0.1',  # Specify your API's version
#     "api_path": "/",  # Specify the path to your API not a root level
#     "enabled_methods": [  # Specify which methods to enable in Swagger UI
#                           'get',
#                           'post',
#                           'put',
#                           'patch',
#                           'delete'
#                           ],
#     "api_key": '1dUayB3xJhdHIJVtxGguefWl1VGKEf', # An API key
#     "is_authenticated": False,  # Set to True to enforce user authentication,
#     "is_superuser": False,  # Set to True to enforce admin only access
# }


import sys
TESTING = False
if sys.argv[1:2] == ['test']:
    TESTING = True
if not TESTING:
    if sys.argv[1:2] == ['jenkins']:
        TESTING = True

CORS_ORIGIN_ALLOW_ALL = True

# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/1.8/howto/static-files/

# STATIC_URL = '/static/'

AWS_ACCESS_KEY_ID = ''
AWS_SECRET_ACCESS_KEY = ''
AWS_BUCKET_URL = ''


if RUNNING_DEV_SERVER:
    SESSION_REDIS_HOST = '127.0.0.1'
    REDIS_CACHE_HOST = '127.0.0.1'
    REDIS_HOST = '127.0.0.1'
else:
    SESSION_REDIS_HOST = ''
    REDIS_CACHE_HOST = ''
    REDIS_HOST = ''


STATIC_URL2 = os.path.join(BASE_DIR, 'static'),

if RUNNING_DEV_SERVER:
    API_URL = 'http://localhost:8000/'
else:
    API_URL = ''


SILKY_PYTHON_PROFILER = True
SILKY_AUTHENTICATION = True  # User must login
SILKY_AUTHORISATION = True  # User must have permissions
SILKY_PERMISSIONS = lambda user: user.is_superuser
SILKY_META = True

if TESTING:
    SILKY_INTERCEPT_PERCENT = 0

# COVERAGE_EXCLUDES = ['backend.utils.redis_lib', 'backend.utils.dynamodb_lib']

