/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {WebsocketService} from './websocket.service';
import {Opcode} from './model/opcode';
import {ObjectType} from './model/object-type';
import {WebsocketListener} from './model/websocket-listener';

describe('WebsocketService', () => {
    var service: WebsocketService;
    var listeners: WebsocketListener[] = [];
    var received: number = 0;

    beforeEach(() => {
        // Gebruik de mock. N.B. daarmee kan ALLEEN de listeners-logica getest worden.
        WebsocketService.test = true;
        service = new WebsocketService(null, null);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should dispatch events to the correct listeners', (done) => {
        const EVENTS = 10000;
        reset();
        for (var i = 0; i < EVENTS; i++) {
            listeners.push(addRandomListener(EVENTS, done));
        }
    });

    it('should not dispatch events to suspended listeners', (done) => {
        reset();
        // TODO ESUITEDEV-25959
        listeners.push(addRandomListener(1, done));
    });

    it('should not dispatch events to removed listeners', (done) => {
        reset();
        // TODO ESUITEDEV-25959
        listeners.push(addRandomListener(1, done));
    });

    // Does not test ANY opcode and/or ANY objectType because the mock doesn't support it (see SessionRegistryTest for those)
    function addRandomListener(expected: number, done): WebsocketListener {
        const OPCODES = [
            Opcode.UPDATED,
            Opcode.DELETED];
        const OBJECT_TYPES = [
            ObjectType.ENKELVOUDIG_INFORMATIEOBJECT,
            ObjectType.TAAK,
            ObjectType.ZAAK,
            ObjectType.ZAAK_ROLLEN,
            ObjectType.ZAAK_INFORMATIEOBJECTEN,
            ObjectType.ZAAK_TAKEN];
        const MAX_DELAY = 512; // ms

        var opcode: Opcode = OPCODES[Math.floor(Math.random() * OPCODES.length)];
        var objectType: ObjectType = OBJECT_TYPES[Math.floor(Math.random() * OBJECT_TYPES.length)];
        var delay: string = Math.floor(Math.random() * MAX_DELAY).toString();
        return service.addListener(opcode, objectType, delay, callback(opcode, objectType, delay, expected, done));
    }

    function callback(opcode: Opcode, objectType: ObjectType, objectId: string, expected: number, done) {
        return event => {
            expect(typeof event.timestamp).toEqual('number');
            expect(event.opcode).toEqual(opcode);
            expect(event.objectType).toEqual(objectType);
            expect(event.objectId).toEqual(objectId);
            expect(event.key).toEqual(opcode + ';' + objectType + ';' + objectId);
            if (++received == expected) {
                done();
                reset();
            }
        };
    }

    // This depends on listeners (do make sure it to keep it consistent with what is registered with the service)
    function reset() {
        for (var i = 0; i < listeners.length; i++) {
            service.removeListener(listeners[i]);
        }
        listeners.length = 0;
        received = 0;
    }
});
