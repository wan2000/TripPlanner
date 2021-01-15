"""Server URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from Tripplaner import views
from django.conf.urls import url

urlpatterns = [
    path('admin/', admin.site.urls),
    path('user/update', views.updateUser),
    #http://192.168.1.3:8080/updateuser
    # {
    # "id": "1",
    # "name": "abc",
    # "dateOfBirth": "12",
    # "placeOfBirth": "23"
    # }
    path('signup', views.Signup),
    #http://192.168.1.3:8080/signup
    # {
    # "username": "1",
    # "password": "1",
    # "name": "12"
    # }
    path('updatetrip', views.updateTrip),
    #http://192.168.1.3:8080/updatetrip
    # {
    # "id": "1", 
    # "tripName": "a", 
    # "budget": 2000000.0, 
    # "startDate": "12", 
    # "endDate": "13", 
    # "departure": "HCM1"
    # }
    path('addtrip', views.addTrip),
    #http://192.168.1.3:8080/addtrip
    # {
    # "idUser": "1", 
    # "tripName": "adasd", 
    # "budget": 2000000.0, 
    # "startDate": "12", 
    # "endDate": "13", 
    # "departure": "HCM1"
    # }
    path('updateexpense', views.updateExpense),
    #http://192.168.1.3:8080/updateexpense
    # {
    # "id": "1", 
    # "expenseName": "an uong", 
    # "cost": 200000.0, 
    # "type": 3
    # }

    path('addexpense', views.addExpense),
    #http://192.168.1.3:8080/addexpense
    # {
    # "idTrip": "1", 
    # "expenseName": "an choi", 
    # "cost": 300000.0, 
    # "type": 3
    # }

    url(r'^login', views.Login),
    #http://192.168.1.3:8080/login?u=fit18&p=fit18
    url(r'^user', views.getUserByID),
    #http://192.168.1.3:8080/getuserbyid?id=1
    url(r'^trips', views.getTripsByUserID),
    #http://192.168.1.3:8080/trip?id=1
    url(r'^gettripbyID', views.getTripByID),
    #http://192.168.1.3:8080/gettripbyID?id=1
    url(r'^expense', views.getExpenseTrip),
    #http://192.168.1.3:8080/expense?id=1
    url(r'^getexpensebyID', views.getExpensebyID)
    #http://192.168.1.3:8080/getexpensebyID?id=1
    
]