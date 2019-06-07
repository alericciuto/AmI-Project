# USAGE
# python detect_blinks.py --shape-predictor shape_predictor_68_face_landmarks.dat --video blink_detection_demo.mp4
# python detect_blinks.py --shape-predictor shape_predictor_68_face_landmarks.dat

# import the necessary packages
from scipy.spatial import distance as dist
from imutils.video import VideoStream
from imutils import face_utils
import argparse
import cv2
import dlib

from timer_interact import Timer, timeout, restart_time


def eye_aspect_ratio(eye):
    A = dist.euclidean(eye[1], eye[5])
    B = dist.euclidean(eye[2], eye[4])

    C = dist.euclidean(eye[0], eye[3])

    ear = (A + B) / (2.0 * C)

    return ear


def run(main_event, driver, pressure_thread):

    pressure_thread.start()

    ap = argparse.ArgumentParser()
    ap.add_argument("-p", "--shape-predictor", required=True,
                    help="path to facial landmark predictor")
    args = vars(ap.parse_args())

    EYE_AR_THRESH = float((driver.get_MAX_EYELID() + driver.get_MIN_EYELID())/2)

    print("[INFO] loading facial landmark predictor...")
    detector = dlib.get_frontal_face_detector()
    predictor = dlib.shape_predictor(args["shape_predictor"])

    (lStart, lEnd) = face_utils.FACIAL_LANDMARKS_IDXS["left_eye"]
    (rStart, rEnd) = face_utils.FACIAL_LANDMARKS_IDXS["right_eye"]

    print("[INFO] starting video stream thread...")
    camera = VideoStream(src=0).start()

    activated_timer = None

    while True:
        if not driver.is_connected():
            pressure_thread.join()
            main_event.acquire()
            main_event.notify()
            main_event.release()
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

            timer = Timer(2.0, timeout, args=(main_event, driver, ear))
            stop = Timer(1.0, restart_time, args=(main_event, driver, ear))
            if ear < EYE_AR_THRESH:
                if activated_timer is None or (not activated_timer.is_waiting() and not activated_timer.is_started()):
                    timer.start()
                    activated_timer = timer
            elif activated_timer is not None:
                if activated_timer.is_waiting():
                    activated_timer.cancel()
                elif activated_timer.is_started():
                    stop.start()
                activated_timer = None

    cv2.destroyAllWindows()
    camera.stop()
