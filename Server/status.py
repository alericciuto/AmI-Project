# verify the max pressure
MAX_PRESSURE = 79
# eyelid depends on the user
MAX_EYELID = 0.40


class Status:
    # finchè sensore non arriva -> valore di default
    def __init__(self, eyelid=-1, pressure=40):
        self.eyelid = eyelid
        self.pressure = pressure

    def set_eyelid(self, eyelid):
        self.eyelid = eyelid * 100 / MAX_EYELID

    def get_eyelid(self):
        if self.eyelid == -1:
            return None
        return self.eyelid

    def set_pressure(self, pressure):
        self.pressure = pressure * 100 / MAX_PRESSURE

    def get_pressure(self):
        if self.pressure == -1:
            return None
        return self.pressure

    def is_awake(self):
        if self.eyelid > 70 or self.eyelid * 0.7 + self.pressure * 0.3 > 60:
            return True
        else:
            return False

    def is_half_asleep(self):
        if 50 <= self.eyelid * 0.7 + self.pressure * 0.3 <= 60:
            return True
        else:
            return False

    def is_asleep(self):
        if self.eyelid < 50 or self.eyelid * 0.7 + self.pressure * 0.3 < 50 or self.pressure < 20:
            return True
        else:
            return False



