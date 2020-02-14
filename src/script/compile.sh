#!/bin/bash
javac -cp .:libs/*:db/:helper/:model/:test/ -d build/ Client.java test/DatabaseHelperTest.java test/VehicleTest.java