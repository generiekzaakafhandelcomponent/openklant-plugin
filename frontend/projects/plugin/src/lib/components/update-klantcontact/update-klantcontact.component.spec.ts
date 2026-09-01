import {ComponentFixture, TestBed} from '@angular/core/testing';
import {of, Subject} from 'rxjs';

import {UpdateKlantcontactComponent} from './update-klantcontact.component';
import {UpdateKlantcontactConfig} from '../../models/update-klantcontact-config';

describe('UpdateKlantcontactComponent', () => {
    let component: UpdateKlantcontactComponent;
    let fixture: ComponentFixture<UpdateKlantcontactComponent>;
    let save$: Subject<void>;

    const emptyConfig = {
        uuid: '',
        resultPvName: '',
    } satisfies Partial<UpdateKlantcontactConfig>;
    const validConfig = {...emptyConfig, uuid: 'x', resultPvName: 'x'} satisfies Partial<UpdateKlantcontactConfig>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [UpdateKlantcontactComponent]
        })
            .overrideTemplate(UpdateKlantcontactComponent, '')
            .compileComponents();

        save$ = new Subject<void>();

        fixture = TestBed.createComponent(UpdateKlantcontactComponent);
        component = fixture.componentInstance;

        component.save$ = save$;
        component.disabled$ = of(false);
        component.pluginId = 'openklant';
        component.prefillConfiguration$ = of(emptyConfig as UpdateKlantcontactConfig);

        fixture.detectChanges();
    });

    afterEach(() => save$.complete());

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should report an incomplete configuration as invalid', () => {
        const emitted: boolean[] = [];
        component.valid.subscribe(valid => emitted.push(valid));

        component.formValueChange(emptyConfig);

        expect(emitted).toEqual([false]);
    });

    it('should report a configuration with every required field as valid', () => {
        const emitted: boolean[] = [];
        component.valid.subscribe(valid => emitted.push(valid));

        component.formValueChange(validConfig);

        expect(emitted).toEqual([true]);
    });

    it('should emit the configuration on save when it is valid', () => {
        const configurations: unknown[] = [];
        component.configuration.subscribe(configuration => configurations.push(configuration));

        component.formValueChange(validConfig);
        save$.next();

        expect(configurations).toEqual([validConfig]);
    });

    it('should not emit the configuration on save when it is invalid', () => {
        const configurations: unknown[] = [];
        component.configuration.subscribe(configuration => configurations.push(configuration));

        component.formValueChange(emptyConfig);
        save$.next();

        expect(configurations).toEqual([]);
    });
});
