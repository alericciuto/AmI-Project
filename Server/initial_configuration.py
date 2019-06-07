from detect_blinks import eye_aspect_ratio
import argparse
from imutils.video import VideoStream
from imutils import face_utils
import cv2
import dlib

from timer_interact import Timer, stop_config
import time


class Face:
    def __init__(self):
        self.MAX_EYELID = 0
        self.MIN_EYELID = 0
        self.nmax = 0
        self.nmin = 0


face = Face()


def run(driver):
    print("\n>> Configuration started")

    ap = argparse.ArgumentParser()
    ap.add_argument("-p", "--shape-predictor", required=True,
                    help="path to facial landmark predictor")
    args = vars(ap.parse_args())

    print("[INFO] loading facial landmark predictor...")
    detector = dlib.get_frontal_face_detector()
    predictor = dlib.shape_predictor(args["shape_predictor"])

    (lStart, lEnd) = face_utils.FACIAL_LANDMARKS_IDXS["left_eye"]
    (rStart, rEnd) = face_utils.FACIAL_LANDMARKS_IDXS["right_eye"]

    print("[INFO] starting video stream thread...")

    camera = VideoStream(src=0).start()

    timerMAX = Timer(3.0, stop, args=(driver,))
    timerMIN = Timer(3.0, stop, args=(driver,))

    while True:
        if driver.get_MIN_EYELID() != -1:
            face.MAX_EYELID = 0
            face.MIN_EYELID = 0
            break

        frame = camera.read()

        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

        rects = detector(gray, 0)

        for rect in rects:
            shape = predictor(gray, rect)
            shape = face_utils.shape_to_np(shape)

            leftEye = shape[lStart:lEnd]
            rightEye = shape[rStart:rEnd]
            leftEAR = eye_aspect_ratio(leftEye)
            rightEAR = eye_aspect_ratio(rightEye)

            ear = (leftEAR + rightEAR) / 2.0

            if driver.get_MAX_EYELID() == -1:
                if face.MAX_EYELID == 0:
                    time.sleep(2.0)
                    driver.beep()
                    timerMAX.start()
                face.MAX_EYELID += ear
                face.nmax += 1
            elif driver.get_MIN_EYELID() == -1:
                if face.MIN_EYELID == 0:
                    time.sleep(3.0)
                    driver.beep()
                    timerMIN.start()
                face.MIN_EYELID += ear
                face.nmin += 1

    cv2.destroyAllWindows()
    camera.stop()


def stop(driver):
    stop_config(driver, face)
