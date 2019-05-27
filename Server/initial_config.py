import status
import time


def send_value(heading, value):


# da implementare in base al tipo di connessione

# PER CONTROLLARE SE I VALORI SONO PRESENTI
def are_values_present(driver):
    if driver.get_min_eyelid() == -1 | | driver.get_max_eyelid() == -1:
        return False
    else:
        return True


# DA USARE QUANDO DOPO AVER RICEVUTI I DATI DAL DATABASE E AVER CONSTATATO CHE NON SONO PRESENTI
# I PARAMETRI DEGLI OCCHI

def find_value(driver):
    # DIRE AD ANDROID CHE ESSENDO L'UTENTE NUOVO BISOGNO PRENDERE I PARAMETRI DEGLI OCCHI
    # E CHE DEVE APPUNTO TENERE GLI OCCHI APERTI
    send_value("print", "EyeLid values not found. I have to take them in order to work.")
    N = 10
    maxvalues = []
    time_to_wait = 50
    send_value("print", "Keep your eyes wide open until i make a sound")
    for i in range(0, N - 1):
        if driver.get_eyelid() is None:
            continue
        maxvalues.insert(driver.get_eyelid())
        time.sleep(time_to_wait)
    send_value("soundNotify", "on")
    send_value("print", "First part of the analysis done")
    # DIRE AD ANDROID CHE L'ANALISI É FINITA L'ANALISI
    max_eyelid = 0
    for value in maxvalues:
        max_eyelid = max_eyelid + value
    max_eyelid = max_eyelid / N

    send_value("print", "Keep your eyes closed until i make a sound")
    # DIRE AD ANDROID DI CHIUDERE GLI OCCHI
    minvalues = []
    for i in range(0, N - 1):
        if driver.get_eyelid() is None:
            continue
        minvalues.insert(driver.get_eyelid())
        time.sleep(time_to_wait)
    # DIRE AD ANDROID CHE L'ANALISI É FINITA L'ANALISI
    send_value("soundNotify", "on")
    send_value("print", "Second part of the analysis done")
    min_eyelid = 0
    for value in minvalues:
        min_eyelid = min_eyelid + value
    min_eyelid = min_eyelid / N
    driver.set_max_eyelid(max_eyelid)
    driver.set_min_eyelid(min_eyelid)

    send_value("print", "Initial configuration completed.")
    send_value("store_maxvalue", max_eyelid)
    send_value("store_minvalue", min_eyelid)
    # canzoncina per dire che configurazione finita
    send_value("configurationCompleted", "on")
    # DIRE AD ANDROID CHE LA CONFIGURAZIONE É COMPLETATA
    # MANDARE AL DATABASE I VALORI


def get_values_from_database(driver):


# da implementare
# bisogna interfacciarsi al database

def initial_config_value(driver):
    get_values_from_database(driver)
    if not are_values_present(driver):
        find_value(driver)
    else:
        send_value("print", "EyeLid values are present. Starting...")
