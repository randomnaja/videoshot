videoshot
=========


    create database video character set utf8;
    # Add user
    grant all privileges on video.* to video identified by 'video123';
    grant all privileges on video.* to video@'localhost' identified by 'video123' ;
    FLUSH PRIVILEGES;