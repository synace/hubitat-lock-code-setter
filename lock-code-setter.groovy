/* No Copyright 2023 @synace this work is public domain under Creative Commons Zero v1.0 Universal */

definition(
    name: "Lock Code Setter",
    namespace: "synace-lock-code-setter",
    author: "synace",
    description: "Sets specified lock codes",
    category: "Convenience",
    iconUrl: "",
    iconX2Url: ""
)

preferences {
    page(name: "mainPage", install: true, uninstall: true) {
        section{
            List varListIn = []
            HashMap varMap = getAllGlobalVars()
            varMap.each { globalVar ->
                varListIn.add(globalVar.key)
            }
            paragraph "<h2>Global Variable</h2><hr>"
            input name: "codeVar", type: "enum", title: "Select variable to sync:", options: varListIn.sort(), required: false, submitOnChange: true

            paragraph "<h2>Locks</h2><hr>"
            input name: "codeSlot", type: "number", title: "Lock Code Slot to set", required: true
            input name: "codeName", type: "text", title: "Lock Code Name to set", required: true
            for (lock in doorLocks) {
                if (!lock.hasCommand("setCode")) paragraph "<p style=\"color:red; padding-top: 30px;\"><strong>NO SUPPORT OF PROGRAMMING CODES THROUGH HUBITAT</strong></p>"
            }
            input "doorLocks", "capability.lock", title: "Which door lock(s) do you want to program", submitOnChange: true, required: true, multiple: true
        }
        section{
            paragraph "<h2>Save Settings</h2><hr>"
            input name: "saveButton", type: "button", title: "Save"
        }
    }
}

def installed() {
    subscribe(location, "variable:${codeVar}", updateProcedure)
}

def updated() {
}

def uninstalled() {
    unsubscribe(location)
}

void updateProcedure(evt = null) {
    doorLocks.each { lock ->
        try {
            lock.setCode(codeposition = codeSlot, pincode = this.getGlobalVar(codeVar).value.toString(), name = codeName)
        } catch(Exception e) {
            log.error "There was an error programming the door lock code, ${e}"
        }
    }
}
