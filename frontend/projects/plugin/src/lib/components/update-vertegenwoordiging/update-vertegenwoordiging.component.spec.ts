import {ComponentFixture, TestBed} from '@angular/core/testing';
import {of, Subject} from 'rxjs';

import {UpdateVertegenwoordigingComponent} from './update-vertegenwoordiging.component';
import {UpdateVertegenwoordigingConfig} from '../../models/update-vertegenwoordiging-config';

describe('UpdateVertegenwoordigingComponent', () => {
    let component: UpdateVertegenwoordigingComponent;
    let fixture: ComponentFixture<UpdateVertegenwoordigingComponent>;
    let save$: Subject<void>;

    const emptyConfig = {
        uuid: '',
        resultPvName: '',
    } satisfies Partial<UpdateVertegenwoordigingConfig>;
    const validConfig = {...emptyConfig, uuid: 'x', resultPvName: 'x'} satisfies Partial<UpdateVertegenwoordigingConfig>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [UpdateVertegenwoordigingComponent]
        })
            .overrideTemplate(UpdateVertegenwoordigingComponent, '')
            .compileComponents();

        save$ = new Subject<void>();

        fixture = TestBed.createComponent(UpdateVertegenwoordigingComponent);
        component = fixture.componentInstance;

        component.save$ = save$;
        component.disabled$ = of(false);
        component.pluginId = 'openklant';
        component.prefillConfiguration$ = of(emptyConfig as UpdateVertegenwoordigingConfig);

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
