package net.atos.zac.mail.model

import net.atos.client.zgw.drc.model.generated.EnkelvoudigInformatieObject
import net.atos.client.zgw.zrc.model.Zaak
import org.flowable.task.api.TaskInfo

class Bronnen private constructor(
    val zaak: Zaak,
    val document: EnkelvoudigInformatieObject?,
    val taskInfo: TaskInfo?
) {
    class Builder {
        private lateinit var zaak: Zaak
        private lateinit var document: EnkelvoudigInformatieObject
        private lateinit var taskInfo: TaskInfo

        fun add(zaak: Zaak): Builder {
            this.zaak = zaak
            return this
        }

        fun add(document: EnkelvoudigInformatieObject): Builder {
            this.document = document
            return this
        }

        fun add(taskInfo: TaskInfo): Builder {
            this.taskInfo = taskInfo
            return this
        }

        fun build(): Bronnen {
            return Bronnen(zaak, document, taskInfo)
        }
    }

    companion object {
        fun fromZaak(zaak: Zaak): Bronnen {
            return Builder().add(zaak).build()
        }
    }
}
