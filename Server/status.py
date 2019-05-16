# verify the max pressure
MAX_PRESSURE = 2000
# eyelid depends on the user
MAX_EYELID = 0.35
MIN_EYELID = 0.15


class Status:
    # finchè sensore non arriva -> valore di default
    def __init__(self, eyelid=-1, pressure=MAX_PRESSURE, previous_status="", flag_pressure_busy=False):
        self.eyelid = eyelid
        self.pressure = pressure
        self.previous_status = previous_status
        self.flag_pressure_busy = flag_pressure_busy

    def set_eyelid(self, eyelid):
        self.eyelid = eyelid

    def get_eyelid(self):
        if self.eyelid == -1:
            return None
        return self.eyelid

    def set_pressure(self, pressure):
        self.pressure = pressure

    def get_pressure(self):
        if self.pressure == -1:
            return None
        return self.pressure

    def is_awake(self):
        while self.flag_pressure_busy:
            continue
        if (self.eyelid - MIN_EYELID / MAX_EYELID - MIN_EYELID) * 85 + (self.pressure / MAX_PRESSURE) * 15 > 60:
            self.previous_status = "awake"
            return True
        else:
            return False

    def is_half_asleep(self):
        while self.flag_pressure_busy:
            continue
        if 50 <= (self.eyelid - MIN_EYELID / MAX_EYELID - MIN_EYELID) * 80 + (self.pressure / MAX_PRESSURE) * 20 <= 60:
            return True
        else:
            return False

    def is_asleep(self):
        while self.flag_pressure_busy:
            continue
        if (self.eyelid - MIN_EYELID / MAX_EYELID - MIN_EYELID) * 80 + (self.pressure / MAX_PRESSURE) * 20 < 50 or \
                (self.pressure / MAX_PRESSURE) * 100 < 20:
            self.previous_status = "asleep"
            return True
        else:
            return False

    def was_asleep(self):
        if self.previous_status == "asleep":
            return True
        else:
            return False

    def flag_busy(self):
        self.flag_pressure_busy = True

    def flag_unbusy(self):
        self.flag_pressure_busy = False

    def flag_is_busy(self):
        return self.flag_pressure_busy
